package com.chengdu.rpc.transport;

import com.chengdu.rpc.Dispatcher;
import com.chengdu.rpc.protocol.MyContent;
import com.chengdu.rpc.util.ServerUtils;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyHttpRpcHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream in = req.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(in);

        try {
            MyContent reqContent = (MyContent) oin.readObject();
            String serviceName = reqContent.getName();
            String methodName = reqContent.getMethodName();

            Object c = Dispatcher.getDis().get(serviceName);
            Class<?> clazz = c.getClass();
            Object res;

            Method method = clazz.getMethod(methodName, reqContent.getParameterTypes());
            res = method.invoke(c, reqContent.getArgs());

            MyContent resContent = new MyContent();
            resContent.setRes(res);

            ServletOutputStream out = resp.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(resContent);

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
