package com.chengdu.quickstart;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

import java.util.List;

public class Consumer2 {

	public static void main(String[] args)throws Exception {

		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-saul001TAG-B");

		consumer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);

		consumer.subscribe("myTopic001", "TAG-A");

		// MessageListenerConcurrently 并发消费，开多个线程
		// MessageListenerOrderly 顺序消费，针对一个queue 开启一个线程， 多个queue开启多个线程

		int consumeThreadMax = 10;
		int consumeThreadMin = 2;
		// 最大开启消费线程数
		consumer.setConsumeThreadMax(consumeThreadMax);

		// 最小消费线程数
		consumer.setConsumeThreadMin(consumeThreadMin);
		consumer.registerMessageListener(new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

				for (MessageExt msg : msgs) {
					
					System.out.println(new String(msg.getBody()));;
				}

				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		
		
		consumer.setMessageModel(MessageModel.CLUSTERING);
		consumer.start();
		
		System.out.println("Consumer TAG-B start...");
	}
}
