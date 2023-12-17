package com.chengdu.quickstart;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Hello world!
 *
 */
public class ProducerDemo {

    @Test
   public void producer() throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384"); // 16k，需要根据 msg 大小调整，尽量触发批次发送, 减少内存碎片
        props.put(ProducerConfig.LINGER_MS_CONFIG, "0");

        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "1048576"); // 1M

        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432"); // 32M
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "60000"); // 60秒
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        props.put(ProducerConfig.SEND_BUFFER_CONFIG, "32768"); // 32k， 设置为-1表示跟随内核设置
        props.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, "32768"); // 32k

        props.put(ProducerConfig.ACKS_CONFIG, "-1");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String topic = "saul-items";
        int total = 1000;
        while(total > 0) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    ProducerRecord<String, String> record = new ProducerRecord<>(topic, "item" + j, "val" + i);
                    Future<RecordMetadata> send = producer.send(record);
                    RecordMetadata recordMetadata = send.get();

                    int partition = recordMetadata.partition();
                    long offset = recordMetadata.offset();

                    System.out.println("key: " + record.key() + "  value: " + record.value() + "  partition: " + partition + "  offset: " + offset);
                }
            }
            total--;
        }
    }
}
