package com.chengdu.rpc;

import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.service.Car;
import com.chengdu.rpc.service.Fly;
import com.chengdu.rpc.service.MyCar;
import com.chengdu.rpc.service.MyFly;
import com.chengdu.rpc.transport.MyHttpRpcHandler;
import com.chengdu.rpc.transport.ServerDecode;
import com.chengdu.rpc.transport.ServerRequestHandler;
import com.chengdu.rpc.util.ServerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class MyRpcServer {

    public static void main(String[] args) {
        new MyRpcServer().startServer();

//        new MyRpcServer().startJettyServer();
    }

    public void startJettyServer() {
        // 跑不成功，需要了解 jetty server创建
        Dispatcher dispatcher = Dispatcher.getDis();
        Car myCar = new MyCar();
        Fly myFly = new MyFly();
        dispatcher.register(Car.class.getName(), myCar);
        dispatcher.register(Fly.class.getName(), myFly);

        // tomcat jetty
        Server server = new Server(new InetSocketAddress(8083));
        ServletContextHandler servletContextHandler = new ServletContextHandler(null, "/");
        server.setHandler(servletContextHandler);

        servletContextHandler.addServlet(MyHttpRpcHandler.class, "/*");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void startServer() {
        Dispatcher dispatcher = Dispatcher.getDis();
        Car myCar = new MyCar();
        Fly myFly = new MyFly();
        dispatcher.register(Car.class.getName(), myCar);
        dispatcher.register(Fly.class.getName(), myFly);

        NioEventLoopGroup boss = new NioEventLoopGroup(10);
        NioEventLoopGroup worker = boss;

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture bind = serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        System.out.println("server accept client port : " + channel.remoteAddress().getPort());
                        ChannelPipeline pipeline = channel.pipeline();
                        if(Config.TRANSPORT_TYPE_USE.equals(Config.TRANSPORT_TYPE_RPC)) {
                            pipeline.addLast(new ServerDecode());
                            pipeline.addLast(new ServerRequestHandler(dispatcher));
                        } else if(Config.TRANSPORT_TYPE_USE.equals(Config.TRANSPORT_TYPE_HTTP_NETTY)
                                || Config.TRANSPORT_TYPE_USE.equals(Config.TRANSPORT_TYPE_HTTP_URL)) {
                            // 传输协议使用 http， netty 提供了一套编解码
                            pipeline.addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(1024 * 512))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            // http 协议， 这个 msg 是完整的 http request
                                            FullHttpRequest request = (FullHttpRequest) msg;
                                            System.out.println(request);

                                            ByteBuf content = request.content();
                                            byte[] data = new byte[content.readableBytes()];
                                            content.readBytes(data);

                                            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
                                            MyContent myContent = (MyContent) in.readObject();

                                            String serviceName = myContent.getName();
                                            String methodName = myContent.getMethodName();
                                            Object c = dispatcher.get(serviceName);
                                            Class<?> clazz = c.getClass();
                                            Object res;

                                            Method method = clazz.getMethod(methodName, myContent.getParameterTypes());
                                            res = method.invoke(c, myContent.getArgs());

                                            MyContent resContent = new MyContent();
                                            resContent.setRes(res);

                                            byte[] resBytes = ServerUtils.serialize(resContent);
                                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, Unpooled.copiedBuffer(resBytes));
                                            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, resBytes.length);
                                            ctx.writeAndFlush(response);
                                        }
                                    });
                        }

                    }
                }).bind(new InetSocketAddress("localhost", 8083));
        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
        }

    }

}
