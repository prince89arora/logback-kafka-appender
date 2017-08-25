package lb.kafka.commons;

import lb.kafka.producer.DeliveryType;

import java.util.Map;

/**
 * Enum to maintain state of kafka configurations and appender.
 *
 * @author prince.arora
 */
public enum ModuleAware {

    CONTEXT;

    private String topic;
    private DeliveryType deliveryType;
    private Map<String, Object> producerConfig;
    private String brokers;
    private String acks;
    private long bufferMemory;
    private int batchSize;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Map<String, Object> getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(final Map<String, Object> producerConfig) {
        this.producerConfig = producerConfig;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public long getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(long bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
