package com.chengdu.quickstart;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

public class ConsumerDemo {

    @Test
    public void consumer() {

        // 基础配置
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());

        // 消费细节
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer_saul_2");

        /**
         *  What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server
         *  earliest: automatically reset the offset to the earliest offset
         *  latest: automatically reset the offset to the latest offset
         *  none: throw exception to the consumer if no previous offset is found for the consumer's group
         */
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        /**
         * 一旦设置自动提交，但是是异步的。会出现如下情况：
         * 1。数据还在执行， 在自动提交前 consumer 挂了， 重起一个 consumer， 参照 offset 的时候会重复消费
         * 2。一个批次的数据还没写数据库成功， 但是该批次的 offset 异步提交了， 此时 consumer 挂了，会丢失消息
         */
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // 自动提交时异步提交， 丢数据 && 重复数据
//        props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000"); // 默认5秒

//        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // poll 拉取数据，弹性， 按需， 拉取多少可以配置

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 可以订阅多个 topic
        consumer.subscribe(List.of("saul-items"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("---onPartitionsRevoked...");
                Iterator<TopicPartition> iterator = partitions.iterator();
                while(iterator.hasNext()) {
                    System.out.println("---onPartitionsRevoked  " + iterator.next().partition());
                }

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("---onPartitionsAssigned...");
                Iterator<TopicPartition> iterator = partitions.iterator();
                while(iterator.hasNext()) {
                    System.out.println("---onPartitionsAssigned  " + iterator.next().partition());
                }
            }
        });

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO); // 0 ~ n

            Set<TopicPartition> partitions = records.partitions(); // 每次 poll 的时候是取多个分区的数据

            // 且每个分区内的数据是有序的
            /**
             * 如果手动提交 offset
             * 1。 按消息进度同步提交
             * 2。 按分区粒度同步提交
             * 3。 按当前 poll 的批次同步提交
             *
             * 如果在多线程方式下
             * 1。 以上1的方式不用多线程
             * 2。 以上2的方式最容易想到多线程
             */
            for (TopicPartition topicPartition : partitions) {
                System.out.println("---------");
                List<ConsumerRecord<String, String>> pRecords = records.records(topicPartition);

                // 在一个批次里，按分区获取 poll 回来的数据
                // 线程按分区处理， 还可以并行按分区处理用多线程的方式
                Iterator<ConsumerRecord<String, String>> pIterator = pRecords.iterator();
                while (pIterator.hasNext()) {
                    ConsumerRecord<String, String> pRecord = pIterator.next();
                    int partition = pRecord.partition();
                    long offset = pRecord.offset();
                    System.out.println("key: " + pRecord.key() + "  val: " + pRecord.value() + "  partition: " + partition + "  offset: " + offset);

                    /**
                    // 按照方式1 提交， 每条消息同步一次
                    TopicPartition tp = new TopicPartition(pRecord.topic(), pRecord.partition());
                    OffsetAndMetadata om = new OffsetAndMetadata(pRecord.offset());
                    Map<TopicPartition, OffsetAndMetadata> offsetsMap = new HashMap<>();
                    offsetsMap.put(tp, om);
                    consumer.commitSync(offsetsMap); // 单线程，多线程都可以
                     */
                }
                /**
                 //
                // 按照方式2 提交， 每个分区提交一次
                OffsetAndMetadata om = new OffsetAndMetadata(pRecords.get(pRecords.size() - 1).offset());
                Map<TopicPartition, OffsetAndMetadata> offsetsMap = new HashMap<>();
                offsetsMap.put(topicPartition, om);
                consumer.commitSync(offsetsMap);
                 */


            }
            consumer.commitSync(); // 这是按照 poll 的批次提交 offset， 第3点

//            Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
//
//            while(iterator.hasNext()) {
//                ConsumerRecord<String, String> record = iterator.next();
//                int partition = record.partition();
//                long offset = record.offset();
//
//                System.out.println("key: " + record.key() + "  val: " + record.value() + "  partition: " + partition + "  offset: " + offset);
//            }
        }
    }
}
