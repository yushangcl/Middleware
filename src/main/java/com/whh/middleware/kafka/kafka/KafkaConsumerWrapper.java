package com.whh.middleware.kafka.kafka;

import com.whh.middleware.kafka.interfaces.MessageListener;
import com.whh.middleware.kafka.interfaces.MqConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huahui.wu
 */
public class KafkaConsumerWrapper implements MqConsumer, InitializingBean, DisposableBean, Runnable {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumerWrapper.class);

    private KafkaConsumer<String, String> consumer;

    private List<MessageListener> listeners;

    private KafkaConsumerConfig config;

    private String topics;

    private Thread thread;
    private volatile boolean running;

    private String env;
    private String appId;

    private static AtomicInteger threadId = new AtomicInteger();

//    @Autowired
//    private MsgRecvMapper msgRecvMapper;

    public KafkaConsumerWrapper(KafkaConsumerConfig config, String topics) {
        listeners = new ArrayList<>();
        this.config = config;
        this.topics = topics;
        env = System.getProperty("env.name");
        appId = System.getProperty("app.id");
    }

    @Override
    public void addListener(MessageListener listener) {
        logger.warn("不要在KafkaConsumerWrapper里直接消费消息!请用EventConsumer");
        listeners.add(listener);
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
        if (config.getEnableAutoCommit() != null) {
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, config.getEnableAutoCommit());
        }
        else {
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        }
        if (config.getAutoCommitIntervalMs() != null) {
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, config.getAutoCommitIntervalMs());
        }
        else {
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        }
        if (config.getSessionTimeoutIntervalMs() != null) {
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, config.getSessionTimeoutIntervalMs());
        }
        else {
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        }
        if (config.getRequestTimeoutIntervalMs() != null) {
            props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.getRequestTimeoutIntervalMs());
        }
        else {
            props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "40000");
        }
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

        consumer = new KafkaConsumer<>(props);
        List<String> topicList = Arrays.asList(topics.split(","));
        List<String> tlist = new ArrayList<>();
        if (env != null) {
            for (String topic : topicList) {
                tlist.add(env + "_" + topic);
            }
        }
        tlist.addAll(topicList);
        consumer.subscribe(tlist);

        while (running) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("topic: {}, key: {}, value: {}", record.topic(), record.key(), record.value());
                    boolean p = false;
                    try {
                        for (MessageListener listener : listeners) {
                            if (record.topic().equals(listener.getTopic())) {
                                listener.onMessage(record.topic(), record.key(), record.value());
                                p = true;
                            }
                        }
                    } catch (Exception e) {
                        logger.error("处理kafka消息时出错", e);
                    }
                    //
//                    if (!p) {
//                        MsgRecv obj = new MsgRecv();
//                        obj.setMsgRecvId(IdWorker.getId());
//                        obj.setTopic(record.topic());
//                        obj.setKey(record.key());
//                        obj.setMsg(record.value());
//                        obj.setAppId(appId);
//                        obj.setStatus((byte) MsgRecvStatus.Unprocess.ordinal());
//                        int r = msgRecvMapper.insert(obj);
//                        EventConfig.incRecvData(r);
//                    }
                }
                consumer.commitSync();
            } catch (Exception e) {
                logger.error("处理kafka消息", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        running = true;
        thread = new Thread(this);
        thread.setName("KafkaConsumer-" + threadId.getAndAdd(1));
        thread.start();
    }

    @Override
    public void destroy(){
        running = false;
    }
}
