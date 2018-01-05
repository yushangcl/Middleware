package com.whh.middleware.kafka;

/**
 * @author huahui.wu
 */
public abstract class MessageListener {

    private String topic;
    private MqConsumer consumer;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MqConsumer consumer) {
        this.consumer = consumer;
    }

    public void init() {
        consumer.addListener(this);
    }

    public abstract void onMessage(String topic, String key, String value) throws Exception;
}
