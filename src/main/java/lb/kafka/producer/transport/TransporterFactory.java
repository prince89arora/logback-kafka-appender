package lb.kafka.producer.transport;

import lb.kafka.producer.DeliveryType;
import org.apache.kafka.clients.producer.Producer;

/**
 * Transporter factory to initialize transporter for production.
 *
 * @author prince.arora
 */
public final class TransporterFactory {

    /**
     * <p>Prepare Transporter for production based on Delivery type {@link DeliveryType}</p>
     * <p>
     * Provide {@link Producer} to transporter that will be used to communicate and
     * send data to kafka broker.
     *</p>
     *
     * @param deliveryType
     *             {@link DeliveryType} to be used for transportation.
     *
     * @param producer
     *             producer for kafka.
     * @return
     *             {@link Transporter}
     *
     */
    public static Transporter getTransporter(DeliveryType deliveryType, Producer<byte[], byte[]> producer) {
        Transporter transporter = null;
        switch (deliveryType) {
            case NORMAL:
                transporter = new NormalTransporter(producer);
                break;

            case YIElD:
                transporter = new YieldTransporter(producer);
                break;

            default:
                transporter = new NormalTransporter(producer);
                break;
        }
        return transporter;
    }
}
