package lb.kafka.producer.transport;

import lb.kafka.producer.DeliveryType;
import org.apache.kafka.clients.producer.Producer;

/**
 * Transporter factory to initialize transporter for production.
 *
 * @author princearora
 */
public final class TransporterFactory {

    /**
     * Prepare Transporter for production based on Delivery type {@link DeliveryType}
     * <p/>
     * Provide {@link Producer} to transporter that will be used to communicate and
     * send data to kafka broker.
     *
     * @param deliveryType
     * @param producer
     * @return
     */
    public static Transporter getTransporter(DeliveryType deliveryType, Producer<byte[], byte[]> producer) {
        Transporter transporter = null;
        switch (deliveryType) {
            case NORMAL:
                transporter = new NormalTransporter(producer);
                break;

            case ASYNC:
                transporter = new YieldTransporter(producer);
                break;

            default:
                break;
        }
        return transporter;
    }
}
