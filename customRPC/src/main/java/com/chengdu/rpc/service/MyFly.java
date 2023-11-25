package com.chengdu.rpc.service;

import com.chengdu.rpc.service.Fly;

public class MyFly implements Fly {
    @Override
    public String xxoo(String msg) {
        return "server, my fly";
    }
}
