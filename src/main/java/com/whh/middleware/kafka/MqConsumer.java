package com.whh.middleware.kafka;

/**
 * @author huahui.wu
 */
public interface MqConsumer {
    void addListener(MessageListener listener);
}
