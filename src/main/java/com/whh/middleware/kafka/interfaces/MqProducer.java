package com.whh.middleware.kafka.interfaces;

/**
 * @author huahui.wu
 */
public interface MqProducer {
    void send(String topic, String keys, Object obj);

    void send(String topic, String keys, String json);
}
