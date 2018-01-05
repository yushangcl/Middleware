package com.whh.middleware.kafka.event.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by shisheng.wang on 17/12/13.
 */
public class MsgSend implements Serializable {
    private Long msgSendId;
    private String topic;
    private String key;
    private String msg;
    private String appId;
    private Byte status;
    private Short trys;
    private Date createTime;
    private Date sendingTime;
    private Date sendTime;

    public Long getMsgSendId() {
        return msgSendId;
    }

    public void setMsgSendId(Long msgSendId) {
        this.msgSendId = msgSendId;
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

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
