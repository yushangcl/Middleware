package com.whh.middleware.kafka.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * kafka 消费者
 *
 * @author huahui.wu
 */
public class ConsumerDemo {

    public static void main(String[] args) {
        Properties props = new Properties();
        //用于建立与kafka集群连接的host/port组 格式： host1:port1, host2:port3...
        props.put("bootstrap.servers", "kafka.likie.win:9092");
        //用来唯一标识consumer进程所在组的字符串，如果设置同样的group id，表示这些processes都是属于同一个consumer group
        props.put("group.id", "test");
        //如果为真，consumer所fetch的消息的offset将会自动的同步到zookeeper。这项提交的offset将在进程挂掉时，由新的consumer使用
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        //key的序列化方式，若是没有设置，同serializer.class
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //value序列化类方式
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        // 消费哪些topic，可以同时消费多个 Arrays.asList("topic1","topic2")
        consumer.subscribe(Arrays.asList("test"));

        //Pull 拉取消息（从代理中）
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }

    }
}
