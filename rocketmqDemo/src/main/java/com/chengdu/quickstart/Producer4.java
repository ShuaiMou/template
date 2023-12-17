package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class Producer4 {

	public static void main(String[] args)throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("test-group");

		producer.setNamesrvAddr("192.168.0.100:9876");
		producer.start();

		Message message = new Message("myTopic003", "TAG-B","KEY-xx","xxooxx".getBytes());

		producer.send(message);
		
		producer.shutdown();
		System.out.println("shutdown");
		
	}
}
