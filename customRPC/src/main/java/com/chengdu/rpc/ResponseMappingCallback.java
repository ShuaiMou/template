package com.chengdu.rpc;

import com.chengdu.rpc.util.PackMsg;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseMappingCallback {
    static ConcurrentHashMap<Long, CompletableFuture<Object>> mappings = new ConcurrentHashMap<>();

    public static void registerCallback(long requestID, CompletableFuture<Object> future) {
        mappings.put(requestID, future);
    }

    public static void runCallback(PackMsg msg) {
        mappings.get(msg.getHeader().getRequestID()).complete(msg.getContent().getRes());
        removerCallbackTask(msg.getHeader().getRequestID());
    }

    private static void removerCallbackTask(long requestID) {
        mappings.remove(requestID);
    }
}
