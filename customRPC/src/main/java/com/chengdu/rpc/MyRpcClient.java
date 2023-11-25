package com.chengdu.rpc;

import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.protocol.MyHeader;
import com.chengdu.rpc.proxy.MyProxy;
import com.chengdu.rpc.service.Car;
import com.chengdu.rpc.transport.ClientFactory;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRpcClient {

    public static void main(String[] args) {
        MyRpcClient myRpcClient = new MyRpcClient();
        int size = 20;
        CountDownLatch countDownLatch = new CountDownLatch(size);

        AtomicInteger atomicInteger = new AtomicInteger();
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    myRpcClient.get(atomicInteger.getAndIncrement());
                    countDownLatch.countDown();
                }
            });
        }

        for (int i = 0; i < size; i++) {
            threads[i].start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void get(int id) {
        Car car = MyProxy.proxyGet(Car.class);// 动态代理实现
        String ooxx = car.ooxx("hello car " + id);
        System.out.println("res :" + ooxx + " ----> send id :" + id);

//        Fly fly = proxyGet(Fly.class);// 动态代理实现
//        fly.xxoo("hello fly");
    }


}