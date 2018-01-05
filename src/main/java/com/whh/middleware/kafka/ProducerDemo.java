package com.whh.middleware.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * kafka 生产者
 *
 * @author huahui.wu
 */
public class ProducerDemo {

    public static void main(String[] args) {
        Properties props = new Properties();
        //用于建立与kafka集群连接的host/port组。数据将会在所有servers上均衡加载，不管哪些server是指定用于bootstrapping。这个列表仅仅影响初始化的hosts（用于发现全部的servers）
        props.put("bootstrap.servers", "kafka.likie.win:9092");
        //producer需要server接收到数据之后发出的确认接收的信号，此项配置就是指procuder需要多少个这样的确认信号。
        // 此配置实际上代表了数据备份的可用性。
        // 以下设置为常用选项：
        // 1）acks=0： 设置为0表示producer不需要等待任何确认收到的信息。副本将立即加到socket buffer并认为已经发送。没有任何保障可以保证此种情况下server已经成功接收数据，同时重试配置不会发生作用（因为客户端不知道是否失败）回馈的offset会总是设置为-1；
        // 2）acks=1： 这意味着至少要等待leader已经成功将数据写入本地log，但是并没有等待所有follower是否成功写入。这种情况下，如果follower没有成功备份数据，而此时leader又挂掉，则消息会丢失。
        // 3）acks=all： 这意味着leader需要等待所有备份都成功写入日志，这种策略会保证只要有一个备份存活就不会丢失数据。这是最强的保证。
        props.put("acks", "all");
        //设置大于0的值将使客户端重新发送任何数据，一旦这些数据发送失败。注意，这些重试与客户端接收到发送错误时的重试没有什么不同。允许重试将潜在的改变数据的顺序，如果这两个消息记录都是发送到同一个partition，则第一个消息失败第二个发送成功，则第二条消息会比第一条消息出现要早。
        props.put("retries", 0);
        //producer将试图批处理消息记录，以减少请求次数。这将改善client与server之间的性能。这项配置控制默认的批量处理消息字节数。
        props.put("batch.size", 16384);
        //这项设置将通过增加小的延迟来完成–即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理。
        props.put("linger.ms", 1);
        //producer可以用来缓存数据的内存大小。
        props.put("buffer.memory", 33554432);
        //key的序列化方式，若是没有设置，同serializer.class
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //value序列化类方式
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer<String, String> producer = new KafkaProducer<>(props);

        //特指 Kafka 处理的消息源（feeds of messages）的不同分类
        String topic = "test";
        for (int i = 0; i < 100; i++) {
            //Push key value
            producer.send(new ProducerRecord<String, String>(topic, Integer.toString(i), Integer.toString(i)));
        }
        //Push value
        producer.send(new ProducerRecord<String, String>(topic, "Finish"));

        //关闭连接
        producer.close();
    }
}
