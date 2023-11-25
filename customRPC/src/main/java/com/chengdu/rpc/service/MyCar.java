package com.chengdu.rpc.service;

import com.chengdu.rpc.service.Car;

public class MyCar implements Car {
    @Override
    public String ooxx(String msg) {
        return "server my car impl " + msg;
    }
}
