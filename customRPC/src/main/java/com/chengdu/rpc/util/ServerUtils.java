package com.chengdu.rpc.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServerUtils {
    static ByteArrayOutputStream out = new ByteArrayOutputStream();

    public static synchronized byte[] serialize(Object object) {
        out.reset();
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(out);
            oout.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] res = out.toByteArray();
        return res;
    }
}
