package com.chengdu.quickstart;

import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class Producer6Transaction {

	public static void main(String[] args)throws Exception {

		TransactionMQProducer producer = new TransactionMQProducer("test-group-transaction");

		producer.setNamesrvAddr(Constant.NAME_SERVER_ADDR);
		producer.setTransactionListener(new TransactionListener() {
			@Override
			public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
				// 执行本地事务
				System.out.println("======== executeLocalTransaction ======");
				System.out.println("executeLocalTransaction -> msg = " + new String(msg.getBody()));
				System.out.println("executeLocalTransaction -> transactionId = " + msg.getTransactionId());

				/**
				 * a()
				 * b()  -> 发消息
				 * c()
				 */
				return LocalTransactionState.UNKNOW;
			}

			@Override
			public LocalTransactionState checkLocalTransaction(MessageExt msg) {
				// broker 端回调，检查事务
				System.out.println("======== checkLocalTransaction ======");
				System.out.println("checkLocalTransaction -> msg = " + new String(msg.getBody()));
				System.out.println("checkLocalTransaction -> transactionId = " + msg.getTransactionId());

				// 等会儿再检查
				return LocalTransactionState.UNKNOW;

				// 事务执行成功
				// return LocalTransactionState.COMMIT_MESSAGE;

				// 回滚消息
//				return LocalTransactionState.ROLLBACK_MESSAGE;
			}
		});
		producer.start();
		

		Message message = new Message("myTopic006", "TAG-T","KEY-transaction","xxooxx-transaction".getBytes());
		SendResult sendResult = producer.sendMessageInTransaction(message, null);
		System.out.println("sendResult = " + sendResult);

		System.out.println("shutdown");
		
	}
}
