package com.chengdu.rpc.transport;

import com.chengdu.rpc.Dispatcher;
import com.chengdu.rpc.util.PackMsg;
import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.protocol.MyHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.chengdu.rpc.util.ServerUtils.serialize;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {

    Dispatcher dispatcher;
    public ServerRequestHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PackMsg packMsg = (PackMsg) msg;
        String ioThreadName = Thread.currentThread().getName();
        System.out.println(packMsg);

        // 直接在当前线程 处理 io 和业务返回
//        ctx.executor().execute();
        // 使用 netty 自己到 eventloop 来处理业务返回
        ctx.executor().parent().next().execute(new Runnable() {
            @Override
            public void run() {
//                String executorThreadName = Thread.currentThread().getName();

                String serviceName = packMsg.getContent().getName();
                String methodName = packMsg.getContent().getMethodName();
                Object targetObj = dispatcher.get(serviceName);
                Class<?> clazz = targetObj.getClass();
                String res;
                try {
                    Method method = clazz.getMethod(methodName, packMsg.getContent().getParameterTypes());
                    res = (String) method.invoke(targetObj, packMsg.getContent().getArgs());
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                MyContent content = new MyContent();
//                String res = ioThreadName + " -> " + executorThreadName + " -> " + packMsg.getContent().args[0];
                content.setRes(res);
                byte[] bodyBytes = serialize(content);

                MyHeader header = new MyHeader();
                header.setFlag(0x14141424);
                header.setRequestID(packMsg.getHeader().getRequestID());
                header.setDataLen(bodyBytes.length);
                byte[] headerBytes = serialize(header);

                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(headerBytes.length + bodyBytes.length);
                byteBuf.writeBytes(headerBytes);
                byteBuf.writeBytes(bodyBytes);
                ctx.writeAndFlush(byteBuf);
            }
        });

    }
}
