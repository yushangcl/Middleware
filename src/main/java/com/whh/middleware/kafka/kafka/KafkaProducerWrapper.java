package com.whh.middleware.kafka.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author huahui.wu
 */
public class KafkaProducerWrapper implements MqProducer {

    private static Logger logger = LoggerFactory.getLogger(KafkaProducerWrapper.class);

    private KafkaProducer<String, String> producer;

    public KafkaProducerWrapper(KafkaProducerConfig config) {
        Properties props = new Properties();
        //此处配置的是kafka的端口
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getServers());
        if (config.getClientId() != null) {
            props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        }
        if (config.getAcks() != null) {
            props.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
        }
        if (config.getRetries() != null) {
            props.put(ProducerConfig.RETRIES_CONFIG, config.getRetries());
        }
        if (config.getBatchSize() != null) {
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getBatchSize());
        }
        if (config.getLingerMs() != null) {
            props.put(ProducerConfig.LINGER_MS_CONFIG, config.getLingerMs());
        }
        if (config.getMaxInFlight() != null) {
            //默认设置为1,安全点儿.
            props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, config.getMaxInFlight()); //"1"
        }

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    public void send(String topic, String keys, Object obj){
        producer.send(new ProducerRecord<>(topic, keys, JSONObject.toJSONString(obj)));
        //producer.flush();
    }

    @Override
    public void send(String topic, String keys, String json){
        producer.send(new ProducerRecord<>(topic, keys, json));
        //producer.flush();
    }

    public void send(String topic, String keys, Object obj, Callback callback){
        producer.send(new ProducerRecord<>(topic, keys, JSONObject.toJSONString(obj)), callback);
        //producer.flush();
    }

    public void send(String topic, String keys, String json, Callback callback){
        producer.send(new ProducerRecord<>(topic, keys, json), callback);
        //producer.flush();
    }

    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
