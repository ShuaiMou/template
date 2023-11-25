package com.chengdu.rpc.transport;

import com.chengdu.rpc.util.PackMsg;
import com.chengdu.rpc.ResponseMappingCallback;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientResponseHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PackMsg packMsg = (PackMsg)msg;

        ResponseMappingCallback.runCallback(packMsg);


    }

}
