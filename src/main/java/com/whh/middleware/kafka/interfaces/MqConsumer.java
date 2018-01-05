package com.whh.middleware.kafka.interfaces;

/**
 * @author huahui.wu
 */
public interface MqConsumer {
    void addListener(MessageListener listener);
}
