package lb.kafka.logback.appender;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lb.kafka.commons.KafkaHelper;
import lb.kafka.commons.ModuleAware;
import lb.kafka.encoder.KafkaMessageEncoder;
import lb.kafka.encoder.PatternBasedMessageEncoder;
import lb.kafka.producer.DeliveryType;
import lb.kafka.producer.FailedCallBack;
import lb.kafka.producer.ProductionFactory;
import lb.kafka.producer.transport.Transporter;

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
 * @author princearora
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
            ModuleAware.CONTEXT.setProducerConfig(KafkaHelper.prepareConfiguration());
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

        /**
         * default encoder if nothing provided in configuration.
         */
        if (this.encoder == null) {
            this.encoder = new PatternBasedMessageEncoder<E>();
            this.encoder.start();
        }

        return status;
    }
}
