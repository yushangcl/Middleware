package com.whh.middleware.kafka.demo;

import com.alibaba.fastjson.JSONObject;
import com.whh.middleware.kafka.event.EventProducer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author huahui.wu.
 *         Created on 2018/1/5.
 */
@Service
public class KafkaTestProducer {

    @Resource
    private EventProducer producer;

    public void test() throws Exception {
        //发生object keys考虑幂等性
        producer.send("test", "keys", new HashMap<>());
        producer.send("test", "keys", new JSONObject());
    }
}
