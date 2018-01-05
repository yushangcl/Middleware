package com.whh.middleware.kafka.event;

import com.whh.middleware.kafka.MessageListener;
import com.whh.middleware.kafka.MqConsumer;
import com.whh.middleware.kafka.event.dao.MsgRecvMapper;
import com.whh.middleware.kafka.event.enums.MsgRecvStatus;
import com.whh.middleware.kafka.event.model.MsgRecv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shisheng.wang on 17/12/20.
 */
public class EventConsumer implements MqConsumer, InitializingBean, DisposableBean, Runnable {

    private final static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private List<MessageListener> listeners;

    private Thread thread;
    private volatile boolean running;

    @Autowired
    private MsgRecvMapper msgRecvMapper;

    private static AtomicInteger threadId = new AtomicInteger();
    private String env;

    private int fetchSize = 10;
    private int expireSecond = 120;
    private short maxTrys = 3;
    private int retryIntervalSecond = 3;
    private int idleIntervalMs = 30000;
    private int keepSecond = 60 * 60 * 24 * 1;

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

    public short getMaxTrys() {
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

    /**
     * 已经消费成功的数据保留多少秒.
     * @return
     */
    public int getKeepSecond() {
        return keepSecond;
    }

    public void setKeepSecond(int keepSecond) {
        this.keepSecond = keepSecond;
    }

    /**
     * 没有事件时,隔多少毫秒检查一次数据库
     * @return
     */
    public int getIdleIntervalMs() {
        return idleIntervalMs;
    }
    /**
     * 没有事件时,隔多少毫秒检查一次数据库
     * @return
     */
    public void setIdleIntervalMs(int idleIntervalMs) {
        this.idleIntervalMs = idleIntervalMs;
    }

    public EventConsumer() {
        listeners = new ArrayList<>();
        env = System.getProperty("env.name");
        if (env != null) {
            env = env + "_";
        }
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void run(){
        long lastIdle = System.currentTimeMillis();
        long lastDelTime = System.currentTimeMillis();
        while(running){
            try {
                if (!EventConfig.hasRecvData()) {
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
                    int r = msgRecvMapper.deletePast((byte) MsgRecvStatus.Processed.ordinal(), keepSecond);
                    if (logger.isDebugEnabled()) {
                        logger.debug("delete recv message. count {}", r);
                    }
                    lastDelTime = System.currentTimeMillis();
                }

                boolean hasError = false;

                List<MsgRecv> msgRecvList = msgRecvMapper.listUnprocess((byte) MsgRecvStatus.Unprocess.ordinal(), retryIntervalSecond, (byte) MsgRecvStatus.Processing.ordinal(), expireSecond, maxTrys, fetchSize);
                for (MsgRecv msgRecv: msgRecvList) {
                    int r = msgRecvMapper.updateProcessing(msgRecv.getMsgRecvId(), (byte) MsgRecvStatus.Processing.ordinal(), msgRecv.getStatus());
                    if (r > 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("begin to process message, id:{} topic:{} key:{} value:{}", msgRecv.getMsgRecvId(), msgRecv.getTopic(), msgRecv.getKey(), msgRecv.getMsg());
                        }
                        try {
                            String topic = msgRecv.getTopic();
                            if (env != null && topic.startsWith(env)) {
                                topic = topic.substring(env.length());
                            }
                            boolean p = false;
                            for (MessageListener listener : listeners) {
                                if (topic.equals(listener.getTopic())) {
                                    listener.onMessage(topic, msgRecv.getKey(), msgRecv.getMsg());
                                    p = true;
                                }
                            }
                            if (p) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("process message success. id:{} topic:{} key:{}", msgRecv.getMsgRecvId(), msgRecv.getTopic(), msgRecv.getKey());
                                }
                                msgRecvMapper.updateProcessed(msgRecv.getMsgRecvId(), (byte) MsgRecvStatus.Processed.ordinal());
                            } else {
                                logger.warn("process message failed! no consumer for topic:{}", topic);
                                msgRecvMapper.updateFail(msgRecv.getMsgRecvId(), (byte) MsgRecvStatus.Unprocess.ordinal());
                            }
                        } catch (Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("process message failed! id:{} topic:{} key:{}", msgRecv.getMsgRecvId(), msgRecv.getTopic(), msgRecv.getKey());
                            }
                            msgRecvMapper.updateFail(msgRecv.getMsgRecvId(), (byte) MsgRecvStatus.Unprocess.ordinal());
                            hasError = true;
                        }
                    }
                    EventConfig.decRecvData(1);
                }
                if (msgRecvList.size() >= fetchSize) {
                    // 如果还有数据
                    if (!EventConfig.hasRecvData()) {
                        EventConfig.incRecvData(1);
                    }
                } else {
                    if (hasError) {
                        lastIdle = System.currentTimeMillis() - idleIntervalMs + retryIntervalSecond * 1020;
                    } else {
                        lastIdle = System.currentTimeMillis();
                    }
                    if (EventConfig.hasRecvData()) {
                        EventConfig.decRecvData(1000000000);
                    }
                }
                Thread.sleep(100);
            } catch (Exception e) {
                logger.error("处理Event接收消息出错", e);
                // 主要因为数据库连不上,防止一直重试
                if (EventConfig.hasRecvData()) {
                    EventConfig.decRecvData(1000000000);
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        running = true;
        this.thread = new Thread(this);
        thread.setName("MqEventRecvProcess-" + threadId.getAndAdd(1));
        this.thread.start();
    }

    @Override
    public void destroy(){
        running = false;
    }
}
