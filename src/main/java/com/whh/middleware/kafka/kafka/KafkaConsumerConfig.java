package com.whh.middleware.kafka.kafka;

/**
 * @author huahui.wu
 */
public class KafkaConsumerConfig {
    private String servers;
    private String groupId;
    private Boolean enableAutoCommit;
    private Integer autoCommitIntervalMs;
    private Integer sessionTimeoutIntervalMs;
    private Integer requestTimeoutIntervalMs;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(Boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public Integer getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    public void setAutoCommitIntervalMs(Integer autoCommitIntervalMs) {
        this.autoCommitIntervalMs = autoCommitIntervalMs;
    }

    public Integer getSessionTimeoutIntervalMs() {
        return sessionTimeoutIntervalMs;
    }

    public void setSessionTimeoutIntervalMs(Integer sessionTimeoutIntervalMs) {
        this.sessionTimeoutIntervalMs = sessionTimeoutIntervalMs;
    }

    public Integer getRequestTimeoutIntervalMs() {
        return requestTimeoutIntervalMs;
    }

    public void setRequestTimeoutIntervalMs(Integer requestTimeoutIntervalMs) {
        this.requestTimeoutIntervalMs = requestTimeoutIntervalMs;
    }
}
