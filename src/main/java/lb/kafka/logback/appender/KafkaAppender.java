package lb.kafka.logback.appender;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lb.kafka.commons.ModuleAware;
import lb.kafka.encoder.KafkaMessageEncoder;
import lb.kafka.encoder.PatternBasedMessageEncoder;
import lb.kafka.producer.DeliveryType;
import lb.kafka.producer.FailedCallBack;
import lb.kafka.producer.ProductionFactory;
import lb.kafka.producer.transport.Transporter;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.COMPRESSION_TYPE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Logback Appender to send log messages to Apache Kafka brokers.
 *
 * <ul>
 *     <li><b>topic </b> Topic name for kafka brokers</li>
 *     <li><b>deliveryType </b> Type of deliver to send payload to kafka brokers</li>
 *     <li><b>brokers </b> Comma separated string for for list of broker servers</li>
 *     <li><b>encoder </b> {@link KafkaMessageEncoder} encoder to encode log event message</li>
 * </ul>
 *
 * @author prince.arora
 */
public class KafkaAppender<E> extends UnsynchronizedAppenderBase<E> {

    /**
     * kafka topic
     */
    private String topic;

    /**
     * delivery type to transportation.
     */
    private String deliveryType;

    /**
     * kafka broker servers.
     */
    private String brokers;

    /**
     * Acknowledgments the producer.
     */
    private String acks = "1";

    /**
     * Memory to be used for buffer records.
     */
    private long bufferMemory = 33554432;

    /**
     * Specify the final compression type for a given topic.
     * This configuration accepts the standard compression
     * codecs ('gzip', 'snappy', 'lz4'). It additionally accepts
     * 'uncompressed' which is equivalent to no compression; and
     * 'producer' which means retain the original compression codec
     * set by the producer.
     */
    private String compressionType = "producer";

    /**
     * Setting a value greater than zero will cause the client to resend
     * any record whose send fails with a potentially transient error.
     * Default value is 0.
     */
    private int retries = 0;

    /**
     * Close idle connections after the number of milliseconds specified by this config.
     */
    private long maxIdleConnectionTime = 540000;

    /**
     * Size for batch records.
     */
    private int batchSize = 16384;

    private KafkaMessageEncoder<E> encoder;

    private Transporter transporter;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public void setBrokers(String[] brokers) {
        if (brokers != null) {
            for(String node : brokers) {
                if (this.brokers == null || this.brokers.equals("")) {
                    this.brokers = node;
                } else {
                    this.brokers = "," + node;
                }
            }
        }
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

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public long getMaxIdleConnectionTime() {
        return maxIdleConnectionTime;
    }

    public void setMaxIdleConnectionTime(long maxIdleConnectionTime) {
        this.maxIdleConnectionTime = maxIdleConnectionTime;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public KafkaMessageEncoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(KafkaMessageEncoder<E> encoder) {
        this.encoder = encoder;
    }

    public void start() {
        /**
         * Check if basic configurations are preset
         * Initialize all the configuration and store in {@link ModuleAware}
         */
        boolean status = this.initialize();

        if (status) {
            ModuleAware.CONTEXT.setProducerConfig(this.prepareConfiguration());
            ProductionFactory.build();
            this.transporter = ProductionFactory.transporter();
            this.started = true;
            super.start();
        }
    }

    public void stop() {
        super.stop();
        /**
         * Destroy productionFactory
         */
        if (this.transporter != null) {
            try {
                ProductionFactory.destroy();
            } catch (Exception ex) {
                this.addWarn("Unable to shutdown kafka production", ex);
            }
        }
    }

    /**
     * The Logging event and the payload will be provided to the transporter
     * and will be forwarded to kafka brokers.
     *
     * @param event
     *                  Log event.
     */
    @Override
    protected void append(E event) {
        final byte[] payload = this.encoder.doEncode(event);
        this.transporter.transport(payload, new FailedCallBack());
    }

    /**
     * Understanding delivery type configure in appender configuration and
     * preparing {@link DeliveryType}
     *
     * @return
     */
    private DeliveryType getDeliveryOption() {
        //Normal delivery type will be used in case of no delivery type selected
        DeliveryType deliveryOption = DeliveryType.NORMAL;

        for (DeliveryType deliveryType : DeliveryType.values()) {
            if (deliveryType.toString().equals(this.deliveryType)) {
                deliveryOption = deliveryType;
            }
        }
        return deliveryOption;
    }

    /**
     * Chack basic configuration requirments for appender to start and update
     * {@link ModuleAware}
     *
     * @return
     */
    private boolean initialize() {
        boolean status = true;
        if (this.topic == null || this.topic.equals("")) {
            addError("Unable to find any topic. Topic is required to connect and deliver packages to brokers.");
            status = false;
        }

        if (this.brokers == null || this.brokers.equals("")) {
            addError("Unable to find any broker. Specify broker(s) host and port to connect.");
            status = false;
        }

        ModuleAware.CONTEXT.setTopic(this.topic);
        ModuleAware.CONTEXT.setBrokers(brokers);
        ModuleAware.CONTEXT.setDeliveryType(this.getDeliveryOption());
        ModuleAware.CONTEXT.setAcks(this.acks);
        ModuleAware.CONTEXT.setBufferMemory(this.bufferMemory);
        ModuleAware.CONTEXT.setBatchSize(this.batchSize);
        ModuleAware.CONTEXT.setCompressionType(this.compressionType);
        ModuleAware.CONTEXT.setRetries(this.retries);
        ModuleAware.CONTEXT.setMaxIdleConnectionTime(this.maxIdleConnectionTime);

        /**
         * default encoder if nothing provided in configuration.
         */
        if (this.encoder == null) {
            this.encoder = new PatternBasedMessageEncoder<E>();
            this.encoder.start();
        }

        return status;
    }

    /**
     * Prepare final configuration map for producer to initiate kafka producer instance
     * Configurations will be picked up from {@link ModuleAware}
     *
     * @return
     */
    private static Map<String, Object> prepareConfiguration() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, ModuleAware.CONTEXT.getBrokers());
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(BATCH_SIZE_CONFIG, ModuleAware.CONTEXT.getBatchSize());
        properties.put(ACKS_CONFIG, ModuleAware.CONTEXT.getAcks());
        properties.put(BUFFER_MEMORY_CONFIG, ModuleAware.CONTEXT.getBufferMemory());
        properties.put(COMPRESSION_TYPE_CONFIG, ModuleAware.CONTEXT.getCompressionType());
        properties.put(RETRIES_CONFIG, ModuleAware.CONTEXT.getRetries());
        properties.put(CONNECTIONS_MAX_IDLE_MS_CONFIG, ModuleAware.CONTEXT.getMaxIdleConnectionTime());
        return properties;
    }
}
