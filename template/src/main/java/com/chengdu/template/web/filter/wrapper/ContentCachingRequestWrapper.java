/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.web.filter.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 解决 requestBody只能读取一次的问题
 */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public ContentCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ContentCachingInputStream(new ByteArrayInputStream(body));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private static class ContentCachingInputStream extends ServletInputStream {

        private final ByteArrayInputStream is;

        private boolean finished = false;

        public ContentCachingInputStream(ByteArrayInputStream is) {
            this.is = is;
        }

        @Override
        public boolean isFinished() {
            return this.finished;
        }

        @Override
        public boolean isReady() {
            return true;
        }


        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("Not yet implemented!");
        }

        @Override
        public int read() throws IOException {
            int data = this.is.read();
            if (data == -1) {
                this.finished = true;
            }
            return data;
        }
    }
}