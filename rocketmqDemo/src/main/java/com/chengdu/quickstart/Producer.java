package com.chengdu.quickstart;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;

public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test-group");

        producer.setNamesrvAddr("192.168.0.100:9876");
        producer.start();

        Message msg1 = new Message("myTopic001", "tagA", "消息1".getBytes());
        Message msg2 = new Message("myTopic001", "tagA", "消息2".getBytes());
        Message msg3 = new Message("myTopic001", "tagA", "消息3".getBytes());

        ArrayList<Message> list = new ArrayList<Message>();
        list.add(msg1);
        list.add(msg2);
        list.add(msg3);

        SendResult sendResult3 = producer.send(list);

        System.out.println(sendResult3);
        producer.shutdown();
        System.out.println("关闭");

    }
}
