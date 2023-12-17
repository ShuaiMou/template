package com.chengdu.quickstart;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

import java.util.List;

public class Consumer2 {

	public static void main(String[] args)throws Exception {

		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-saul001TAG-B");

		consumer.setNamesrvAddr("192.168.0.100:9876");

		consumer.subscribe("myTopic002", "TAG-B");
		
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
