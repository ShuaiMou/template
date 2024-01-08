package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

public class Producer7QueueSelector {

	public static void main(String[] args)throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("test-group");

		producer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);
		producer.start();

		Message message = new Message("myTopic001", "TAG-B","KEY-xx","xxooxx".getBytes());

		producer.send(message,
				// queue 选择器， 向topic中哪个queue写消息
				new MessageQueueSelector() {
			// 手动选择一个queue
			@Override
			public MessageQueue select(
					// 当前topic中包含的所有queue
					List<MessageQueue> mqs,
					// 具体要发的那条消息
					Message msg,

					// 对应send()里的args
					Object arg) {

				// 选择对应的queue
				int index = Integer.parseInt((String) arg) % mqs.size();
				return mqs.get(index);
			}
		}, "333", 2000);
		
		producer.shutdown();
		System.out.println("shutdown");
		
	}
}
