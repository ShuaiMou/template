package com.chengdu.rpc.transport;

import com.chengdu.rpc.Config;
import com.chengdu.rpc.ResponseMappingCallback;
import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.protocol.MyHeader;
import com.chengdu.rpc.util.ServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFactory {

    int poolSize = 1;
    private static final  ClientFactory factory;
    NioEventLoopGroup clientWorker;
    Random rand = new Random();

    static {
        factory = new ClientFactory();
    }

    private ClientFactory() {}

    public static ClientFactory getFactory() {
        return factory;
    }
    // 一个consumer 可以连接很多的 provider, 每个provider 都有自己的 pool， k, v
    ConcurrentHashMap<InetSocketAddress, ClientPool> outBoxes = new ConcurrentHashMap<>();

    public NioSocketChannel getClient(InetSocketAddress address) {
        ClientPool clientPool = outBoxes.get(address);
        if(Objects.isNull(clientPool)) {
            synchronized (outBoxes) {
                if(Objects.isNull(outBoxes)) {
                    outBoxes.putIfAbsent(address, new ClientPool(poolSize));
                    clientPool = outBoxes.get(address);
                }
            }
        }

        int i = rand.nextInt(poolSize);

        if(Objects.nonNull(clientPool.clients[i]) && clientPool.clients[i].isActive()) {
            return clientPool.clients[i];
        }

        synchronized (clientPool.lock[i]) {
            return clientPool.clients[i] = create(address);
        }
    }

    private NioSocketChannel create(InetSocketAddress address) {
        // 基于 netty 的各种客户端创建方式

        clientWorker = new NioEventLoopGroup(1);
        Bootstrap bs = new Bootstrap();

        ChannelFuture connect = bs.group(clientWorker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new ServerDecode());
                        pipeline.addLast(new ClientResponseHandler()); // 解决服务端数据返回给谁, requestID
                    }
                }).connect(address);

        try{
            NioSocketChannel client = (NioSocketChannel) connect.sync().channel();
            return client;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Object> transport(MyContent content) {

        // content 是传输内容， 现在可以用自定义的 rpc 协议传输， 也可以用 http 协议作为载体传输
        // 先手工用 http 协议作为载体， 那这样代表未来可以让 provider 是一个tomcat jetty 基于 http 协议

        CompletableFuture<Object> future = new CompletableFuture<>();

        if(Config.TRANSPORT_TYPE_USE.equals(Config.TRANSPORT_TYPE_RPC)) {
            byte[] msgBody = ServerUtils.serialize(content);

            MyHeader header = MyHeader.createHeader(msgBody);
            byte[] msgHeader = ServerUtils.serialize(header);
            System.out.println("header.length = " + msgHeader.length);

            ResponseMappingCallback.registerCallback(header.getRequestID(), future);

            NioSocketChannel clientChannel = factory.getClient(new InetSocketAddress("localhost", 8083));


            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(msgHeader.length + msgBody.length);
            byteBuf.writeBytes(msgHeader);
            byteBuf.writeBytes(msgBody);
            clientChannel.writeAndFlush(byteBuf);
        } else if (Config.TRANSPORT_TYPE_USE.equals(Config.TRANSPORT_TYPE_HTTP_URL)) {
            // 使用 http 协议为载体
            // 1。 用 URL 现成工具 （包含 http 的编解码， 发送， socket， 连接）
            urlTS(content, future);
        } else {
            // 2. 自己操心: on netty (io 框架) + 已经提供的 http 相关的编解码
            nettyTS(content, future);
        }
        return future;
    }

    private static void nettyTS(MyContent content, CompletableFuture<Object> future) {
        // 每个请求对应一个连接
        // 1。 通过 netty 建立 io 连接
        NioEventLoopGroup group = new NioEventLoopGroup(1); // 定义到外面
        Bootstrap bootstrap = new Bootstrap();
        Bootstrap client = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(1024 * 512))
                                .addLast(new ChannelInboundHandlerAdapter() {

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 3. 接收 预埋回调， 根据 netty 对 socket io 事件的响应
                                        FullHttpResponse response = (FullHttpResponse) msg;

                                        ByteBuf content = response.content();
                                        byte[] data = new byte[content.readableBytes()];
                                        content.readBytes(data);

                                        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
                                        MyContent myContent = (MyContent) in.readObject();
                                        future.complete(myContent.getRes());
                                    }
                                });
                    }
                });

        try {
            ChannelFuture syncFuture = client.connect(new InetSocketAddress("localhost", 8083)).sync();

            // 2。 发送
            Channel clientChannel = syncFuture.channel();
            byte[] data = ServerUtils.serialize(content);
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, "/", Unpooled.copiedBuffer(data));
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);

            clientChannel.writeAndFlush(request); // 作为 client 端， 向 server 端发送 http request
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private static void urlTS(MyContent content, CompletableFuture<Object> future) {
        Object obj = null;
        try {
            URL url = new URL("http://localhost:8083");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream out = connection.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(content);

            if(connection.getResponseCode() == 200) {
                InputStream in = connection.getInputStream();
                ObjectInputStream oin = new ObjectInputStream(in);
                MyContent myContent =(MyContent) oin.readObject();
                obj = myContent.getRes();
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        future.complete(obj);
    }
}
