package com.chengdu.rpc.protocol;

import java.io.Serializable;
import java.util.UUID;

public class MyHeader implements Serializable {
    // 通信协议

    /**
     * 1. ooxx值
     * 2. UUID
     * 3. DATA_LEN
     */
    int flag; // 32位可以存很多信息

    long DataLen;
    long requestID;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getDataLen() {
        return DataLen;
    }

    public void setDataLen(long dataLen) {
        DataLen = dataLen;
    }

    public long getRequestID() {
        return requestID;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public static MyHeader createHeader(byte[] msg) {
        MyHeader header = new MyHeader();
        int size = msg.length;

        // 0x14 0001 0100
        int flag = 0x14141414;
        long requestId = Math.abs(UUID.randomUUID().getLeastSignificantBits());

        header.setFlag(flag);
        header.setDataLen(size);
        header.setRequestID(requestId);

        return header;
    }

    @Override
    public String toString() {
        return "MyHeader{" +
                "flag=" + flag +
                ", DataLen=" + DataLen +
                ", requestID=" + requestID +
                '}';
    }


}
