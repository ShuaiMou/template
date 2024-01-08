package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class Producer3Oneway {

	public static void main(String[] args)throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("test-group");

		producer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);
		producer.start();

		Message message = new Message("myTopic001", "hello".getBytes());

		producer.sendOneway(message);
		
		System.out.println("shutdown");
		
	}
}
