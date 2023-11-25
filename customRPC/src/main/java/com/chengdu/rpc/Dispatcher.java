package com.chengdu.rpc;

import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {
    static final Dispatcher dispatcher;

    static {
        dispatcher = new Dispatcher();
    }

    private Dispatcher(){}
    ConcurrentHashMap<String, Object> invokeMap = new ConcurrentHashMap<>();

    public void register(String k, Object v) {
        invokeMap.put(k, v);
    }

    public Object get(String k) {
        return invokeMap.get(k);
    }

    public static Dispatcher getDis() {
        return dispatcher;
    }
}
