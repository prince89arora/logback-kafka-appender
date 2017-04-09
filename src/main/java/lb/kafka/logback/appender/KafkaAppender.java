package lb.kafka.logback.appender;

import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lb.kafka.commons.KafkaHelper;
import lb.kafka.commons.ModuleAware;
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
 *     <li><b>layout </b> {@link Layout} layout from logback to encode log event message</li>
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
     * dleivery type to transportation.
     */
    private String deliveryType;

    /**
     * kafka broker servers.
     */
    private String brokers;

    /**
     * logback message layout.
     */
    private Layout<E> layout;

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

    public Layout<E> getLayout() {
        return layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
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

    @Override
    protected void append(E event) {
        byte[] bytes;
        if (this.layout != null) {
            bytes = this.layout.doLayout(event).getBytes();
        } else {
            bytes = event.toString().getBytes();
        }
        this.transporter.transport(bytes, new FailedCallBack());
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
        return status;
    }
}
