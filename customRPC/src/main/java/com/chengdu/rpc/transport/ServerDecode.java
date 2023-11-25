package com.chengdu.rpc.transport;

import com.chengdu.rpc.util.PackMsg;
import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.protocol.MyHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class ServerDecode extends ByteToMessageDecoder {

    // 父类里一定有 channelRead {在 read 前拼接老的 buf --》 decode  -》 剩余留存； 对 out 遍历}
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> out) throws Exception {
        System.out.println("channel start: " + buf.readableBytes());
        int headerSize = 103;
        while(buf.readableBytes() >= headerSize) {
            byte[] bytes = new byte[headerSize];
            buf.getBytes(buf.readerIndex(), bytes); // 从哪里读取，读多少，但是 readindex不变

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader)oin.readObject();

            if(buf.readableBytes() >= (header.getDataLen() + headerSize)) {
                buf.readBytes(headerSize); // 移动指针到 body 开始到位置

                byte[] data = new byte[(int) header.getDataLen()];
                buf.readBytes(data);
                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);

                if(header.getFlag() == 0x14141414) {
                    // 代表请求到服务端，服务端处理
                    MyContent content = (MyContent) doin.readObject();
                    out.add(new PackMsg(header, content));
                } else if(header.getFlag() == 0x14141424) {
                    // 代表返回 客户端处理
                    MyContent content = (MyContent) doin.readObject();
                    out.add(new PackMsg(header, content));
                }

            } else {
                break;
            }

        }
    }
}
