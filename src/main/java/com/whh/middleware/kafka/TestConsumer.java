package com.whh.middleware.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by huahui.wu on 2017/12/27.
 */

public class TestConsumer {

    static final String topic = "test";

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                group1();
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                group2();
            }
        });
//        thread2.start();
        thread.start();



    }

    public static void group1() {
        Properties prop = new Properties();
        prop.put("zookeeper.connect", "kafka.likie.win:2181");
        prop.put("serializer.class", StringEncoder.class.getName());
        prop.put("metadata.broker.list", "kafka.likie.win:9092");
        prop.put("group.id", "group1");
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
        final KafkaStream<byte[], byte[]> kafkaStream = messageStreams.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
        while (iterator.hasNext()) {
            String msg = new String(iterator.next().message());
            System.out.println("收到消息：group1--" + msg);
        }
    }

    public static void group2() {
        Properties prop = new Properties();
        prop.put("zookeeper.connect", "kafka.likie.win:2181");
        prop.put("serializer.class", StringEncoder.class.getName());
        prop.put("metadata.broker.list", "kafka.likie.win:9092");
        prop.put("group.id", "group2");
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
        final KafkaStream<byte[], byte[]> kafkaStream = messageStreams.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
        while (iterator.hasNext()) {
            String msg = new String(iterator.next().message());
            System.out.println("收到消息：group2--" + msg);
        }
    }

}