package com.chengdu.rpc.util;

import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.protocol.MyHeader;

public class PackMsg {
    MyHeader header;
    MyContent content;
    public PackMsg(MyHeader header, MyContent content) {
        this.header = header;
        this.content = content;
    }

    public MyHeader getHeader() {
        return header;
    }

    public void setHeader(MyHeader header) {
        this.header = header;
    }

    public MyContent getContent() {
        return content;
    }

    public void setContent(MyContent content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PackMsg{" +
                "header=" + header +
                ", content=" + content +
                '}';
    }
}
