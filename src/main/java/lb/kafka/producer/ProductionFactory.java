package lb.kafka.producer;

import lb.kafka.commons.ModuleAware;
import lb.kafka.producer.transport.Transporter;
import lb.kafka.producer.transport.TransporterFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

/**
 * Factory class to create {@link Producer} and {@link Transporter}
 * <p/>
 * This will be used ba appender to initialize the producer and transporter.
 * And send payload to kafka brokers based on log events.
 *
 * @author princearora
 */
public class ProductionFactory {

    /**
     * Transformer instace to be used in process
     */
    private static Transporter transporter;

    /**
     * Producer builder to prepare kafka producer instance.
     */
    private static ProducerBuilder producerBuilder;

    /**
     * Disable instance creation
     */
    private ProductionFactory() {}

    public static void build() {
        producerBuilder = new ProducerBuilder();
    }

    /**
     * Prepare and return {@link Transporter} based on the delivery type {@link DeliveryType}
     * configured in appender configuration.
     *
     * @return
     */
    public static Transporter transporter() {
        if (transporter == null) {
            transporter = TransporterFactory.getTransporter(ModuleAware.CONTEXT.getDeliveryType(),
                    producerBuilder.get());
        }
        return transporter;
    }

    /**
     * Destroy Production by removing transporter and closing producer
     */
    public static void destroy() {
        if (producerBuilder != null) {
            producerBuilder.get().close();
        }
        transporter = null;
    }

    public static Producer<byte[], byte[]> getProducer() {
        return producerBuilder.get();
    }

    /**
     * Builder class to create {@link Producer} instance of type {@code byte[]}
     */
    private static class ProducerBuilder {
        private volatile KafkaProducer<byte[], byte[]> producer;

        public Producer<byte[], byte[]> get() {
            if (producer == null) {
                synchronized (this) {
                    if (producer == null) {
                        producer = new KafkaProducer<byte[], byte[]>(ModuleAware.CONTEXT.getProducerConfig());
                    }
                }
            }
            return producer;
        }

    }

}
