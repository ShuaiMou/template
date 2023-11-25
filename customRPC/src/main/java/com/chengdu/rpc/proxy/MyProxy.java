package com.chengdu.rpc.proxy;

import com.chengdu.rpc.Dispatcher;
import com.chengdu.rpc.ResponseMappingCallback;
import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.protocol.MyHeader;
import com.chengdu.rpc.transport.ClientFactory;
import com.chengdu.rpc.util.ServerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MyProxy {
    public static <T>T proxyGet(Class<T> interfaceInfo) {
        // 实现各个版本等动态代理
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};

        return (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 如何设计我们的 consumer 对于 provider 的调用过程

                // 1，调用服务，方法，参数 --》 封装成message

                String name = interfaceInfo.getName();
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();

                MyContent myContent = new MyContent();
                myContent.setName(name);
                myContent.setMethodName(methodName);
                myContent.setParameterTypes(parameterTypes);
                myContent.setArgs(args);
                // 2, requestId + message, 本地要缓存
                // 协议： 【header<>】【body】
                // 3, 连接池：取得连接
                // 4， 发送 --》走IO out --> 走netty( event 驱动)
                // 5, 如果从 io 返回数据了，怎么将代码执行到这里
                CompletableFuture<Object> future = ClientFactory.transport(myContent);

                // （睡眠/回调， 如何让线程停下来，后面还能让他继续执行）
                return future.get();
            }
        });
    }



}
