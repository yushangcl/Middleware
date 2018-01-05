package com.whh.middleware.kafka.event;

import com.alibaba.fastjson.JSONObject;
import com.whh.middleware.kafka.MqProducer;
import com.whh.middleware.kafka.event.dao.MsgSendMapper;
import com.whh.middleware.kafka.event.enums.MsgSendStatus;
import com.whh.middleware.kafka.event.model.MsgSend;


import javax.annotation.Resource;

/**
 * Created by shisheng.wang on 17/12/13.
 */
public class EventProducer implements MqProducer {
    @Resource
    private MsgSendMapper msgSendMapper;

    private String appId;
    private String env;

//    private String[] noPrefixTopic;
//    private String noPrefixTopicString;
//    public String getNoPrefixTopic() {
//        return noPrefixTopicString;
//    }
//
//    /**
//     * 暂时没用上
//     * @param noPrefixTopic
//     */
//    public void setNoPrefixTopic(String noPrefixTopic) {
//        this.noPrefixTopicString = noPrefixTopic;
//        if (noPrefixTopic != null) {
//            this.noPrefixTopic = noPrefixTopic.split(",");
//        }
//    }

    public EventProducer() {
        appId = System.getProperty("app.id");
        env = System.getProperty("env.name");
    }

    @Override
    public void send(String topic, String keys, Object obj) {
        MsgSend record = new MsgSend();
        record.setMsgSendId(IdWorker.getId());
        record.setTopic(getEvnTopic(topic));
        record.setKey(keys);
        record.setMsg(JSONObject.toJSONString(obj));
        record.setAppId(appId);
        record.setStatus((byte) MsgSendStatus.Unsend.ordinal());
        int r = msgSendMapper.insert(record);
        EventConfig.incSendData(r);
    }

    @Override
    public void send(String topic, String keys, String json) {
        MsgSend record = new MsgSend();
        record.setMsgSendId(IdWorker.getId());
        record.setTopic(getEvnTopic(topic));
        record.setKey(keys);
        record.setMsg(json);
        record.setAppId(appId);
        record.setStatus((byte) MsgSendStatus.Unsend.ordinal());
        int r = msgSendMapper.insert(record);
        EventConfig.incSendData(r);
    }

    protected String getEvnTopic(String topic) {
        return env != null ? env + "_" + topic : topic;
    }
}
