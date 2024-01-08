package com.chengdu.common.callback;

@FunctionalInterface
public interface CallbackInterface {

    public void call(String messageId, String replayContent);
}
