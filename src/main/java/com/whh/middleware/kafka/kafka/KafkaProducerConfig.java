package com.whh.middleware.kafka.kafka;

/**
 * @author huahui.wu
 */
public class KafkaProducerConfig {
    private String servers;
    private String clientId;
    private String acks;
    private Integer retries;
    private Integer batchSize;
    private Integer lingerMs;
    private Integer maxInFlight;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(Integer lingerMs) {
        this.lingerMs = lingerMs;
    }

    public Integer getMaxInFlight() {
        return maxInFlight;
    }

    public void setMaxInFlight(Integer maxInFlight) {
        this.maxInFlight = maxInFlight;
    }
}
