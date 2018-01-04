package com.whh.middleware.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class Sender {

    public static void main(String[] args) {
        for (int i =0; i < 10; i ++ ) {
            send();
        }
//        send();
    }

    public static void send() {
        Properties prop = new Properties();
        prop.put("metadata.broker.list", "kafka.likie.win:9092");
        prop.put("serializer.class", "kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(prop);
        Producer<String, String> producer = new<String, String> Producer(producerConfig);
        String topic = "test";
        KeyedMessage<String, String> message = new<String, String> KeyedMessage(topic, "Sam Hello Test message1");
        producer.send(message);
        producer.close();
    }

}
