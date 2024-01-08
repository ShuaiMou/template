package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class Producer4 {

	public static void main(String[] args)throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("test-group");

		producer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);
		producer.start();

		Message message = new Message("myTopic001", "TAG-B","KEY-xx","xxooxx".getBytes());

		producer.send(message);
		
		producer.shutdown();
		System.out.println("shutdown");
		
	}
}
