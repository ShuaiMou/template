package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer2 {

	public static void main(String[] args)throws Exception {

		DefaultMQProducer producer = new DefaultMQProducer("test-group");

		producer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);
		producer.start();

		Message message = new Message("myTopic001", "hello".getBytes());
	
		
	//	producer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendAsyncFailed);
		producer.send(message,new SendCallback() {
			
			@Override
			public void onSuccess(SendResult sendResult) {
				System.out.println("sendResult = " + sendResult);
				
			}
			
			@Override
			public void onException(Throwable e) {
				e.printStackTrace();
				System.out.println("exception");
			}
		});
	
		System.out.println("finish");
		
	}
}
