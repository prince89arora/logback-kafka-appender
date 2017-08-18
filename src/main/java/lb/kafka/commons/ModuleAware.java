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

}
