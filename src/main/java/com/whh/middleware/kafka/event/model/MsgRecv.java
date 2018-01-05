package com.whh.middleware.kafka.event.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by shisheng.wang on 17/12/13.
 */
public class MsgRecv implements Serializable {
    private Long msgRecvId;
    private String topic;
    private String key;
    private String msg;
    private String appId;
    private Byte status;
    private Short trys;
    private Date createTime;
    private Date processingTime;
    private Date processedTime;

    public Long getMsgRecvId() {
        return msgRecvId;
    }

    public void setMsgRecvId(Long msgRecvId) {
        this.msgRecvId = msgRecvId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Short getTrys() {
        return trys;
    }

    public void setTrys(Short trys) {
        this.trys = trys;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Date processingTime) {
        this.processingTime = processingTime;
    }

    public Date getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(Date processedTime) {
        this.processedTime = processedTime;
    }
}
