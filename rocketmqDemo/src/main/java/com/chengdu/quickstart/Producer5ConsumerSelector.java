package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class Producer5ConsumerSelector {

	public static void main(String[] args)throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("test-group");

		producer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);
		producer.start();
		
		for (int i = 1; i <= 100; i++) {
			
			Message message = new Message("myTopic003", "TAG-B","KEY-xx",("xxooxx:" + i ).getBytes());
			message.putUserProperty("age", String.valueOf(i));
			producer.send(message);
		}
		
		producer.shutdown();
		System.out.println("shutdown");
		
	}
}
