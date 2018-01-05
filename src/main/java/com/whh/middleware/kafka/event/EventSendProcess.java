package com.whh.middleware.kafka.event;


import com.whh.middleware.kafka.event.dao.MsgSendMapper;
import com.whh.middleware.kafka.event.enums.MsgRecvStatus;
import com.whh.middleware.kafka.event.enums.MsgSendStatus;
import com.whh.middleware.kafka.event.model.MsgSend;
import com.whh.middleware.kafka.kafka.KafkaProducerWrapper;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shisheng.wang on 17/12/15.
 */
public class EventSendProcess implements InitializingBean, DisposableBean, Runnable {

    private final Logger logger = LoggerFactory.getLogger(EventSendProcess.class);

    private Thread thread;
    private volatile boolean running;

    @Autowired
    private MsgSendMapper msgSendMapper;

    @Resource
    private KafkaProducerWrapper producerWrapper;

    private static AtomicInteger threadId = new AtomicInteger();

    private int fetchSize = 100;
    private int expireSecond = 120;
    private short maxTrys = 3;
    private int retryIntervalSecond = 3;
    private int idleIntervalMs = 30000;
    private int keepSecond = 60 * 60 * 24 * 3;

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }

    public int getMaxTrys() {
        return maxTrys;
    }

    public void setMaxTrys(short maxTrys) {
        this.maxTrys = maxTrys;
    }

    public int getRetryIntervalSecond() {
        return retryIntervalSecond;
    }
    /**
     * 重试的间隔.精确到秒
     * @param retryIntervalSecond
     */
    public void setRetryIntervalSecond(int retryIntervalSecond) {
        this.retryIntervalSecond = retryIntervalSecond;
    }

    public int getIdleIntervalMs() {
        return idleIntervalMs;
    }

    public void setIdleIntervalMs(int idleIntervalMs) {
        this.idleIntervalMs = idleIntervalMs;
    }

    public int getKeepSecond() {
        return keepSecond;
    }

    public void setKeepSecond(int keepSecond) {
        this.keepSecond = keepSecond;
    }

    @Override
    public void run(){
        long lastIdle = System.currentTimeMillis();
        long lastDelTime = System.currentTimeMillis();
        while(running){
            try {
                if (!EventConfig.hasSendData()) {
                    // 每隔30秒扫描一次
                    if (System.currentTimeMillis() - lastIdle > idleIntervalMs) {
                        lastIdle = System.currentTimeMillis();
                    } else {
                        Thread.sleep(100);
                        continue;
                    }
                }

                // 删除过期的数据
                if (System.currentTimeMillis() - lastDelTime > 600000) {
                    int r = msgSendMapper.deletePast((byte) MsgRecvStatus.Processed.ordinal(), keepSecond);
                    if (logger.isDebugEnabled()) {
                        logger.debug("delete send message. count {}", r);
                    }
                    lastDelTime = System.currentTimeMillis();
                }

                List<MsgSend> msgSendList = msgSendMapper.listUnsend((byte) MsgSendStatus.Unsend.ordinal(), retryIntervalSecond, (byte) MsgSendStatus.Sending.ordinal(), expireSecond, maxTrys, fetchSize);
                for (MsgSend msgSend: msgSendList) {
                    int r = msgSendMapper.updateSending(msgSend.getMsgSendId(), (byte) MsgSendStatus.Sending.ordinal(), msgSend.getStatus());
                    if (r > 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("begin to send kafka message, id:{} topic:{} key:{} value:{}", msgSend.getMsgSendId(), msgSend.getTopic(), msgSend.getKey(), msgSend.getMsg());
                        }
                        producerWrapper.send(msgSend.getTopic(), msgSend.getKey(), msgSend.getMsg(), new Callback() {
                            @Override
                            public void onCompletion(RecordMetadata metadata, Exception exception) {
                                if (exception != null) {
                                    // 发送出错,重置状态
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("send fail kafka message! id:{} topic:{} key:{}", msgSend.getMsgSendId(), msgSend.getTopic(), msgSend.getKey());
                                    }
                                    msgSendMapper.updateFail(msgSend.getMsgSendId(), (byte) MsgSendStatus.Unsend.ordinal());
                                } else {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("send success kafka message. id:{} topic:{} key:{}", msgSend.getMsgSendId(), msgSend.getTopic(), msgSend.getKey());
                                    }
                                    msgSendMapper.updateSend(msgSend.getMsgSendId(), (byte) MsgSendStatus.Send.ordinal());
                                }
                            }
                        });
                    }
                    EventConfig.decSendData(1);
                }
                if (msgSendList.size() >= fetchSize) {
                    // 如果还有数据
                    if (!EventConfig.hasSendData()) {
                        EventConfig.incSendData(1);
                    }
                } else {
                    if (EventConfig.hasSendData()) {
                        EventConfig.decSendData(1000000000);
                    }
                    lastIdle = System.currentTimeMillis();
                }
                Thread.sleep(100);
            } catch (Exception e) {
                logger.error("处理Event发送消息出错", e);
                // 主要因为数据库连不上,防止一直重试
                if (EventConfig.hasSendData()) {
                    EventConfig.decSendData(1000000000);
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        running = true;
        this.thread = new Thread(this);
        thread.setName("MqEventSendProcess-" + threadId.getAndAdd(1));
        this.thread.start();
    }

    @Override
    public void destroy(){
        running = false;
    }

}