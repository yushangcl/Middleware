package com.whh.middleware.kafka.demo;


import com.alibaba.fastjson.JSONObject;
import com.whh.middleware.kafka.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huahui.wu.
 *         Created on 2018/1/5.
 */
public class KafkaTestConsumer extends MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(KafkaTestConsumer.class);

    @Override
    public void onMessage(String topic, String key, String value) throws Exception {
        logger.info("t {} k {} v {}", topic, key, value);
        //todo 业务处理
    }
}
