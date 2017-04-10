package lb.kafka.producer.transport;

import org.apache.kafka.clients.producer.Callback;

/**
 * Transporter to transfer message to Apache Kafka broker. Transporter
 * implementation is based on the Delivery Type provider in appender
 * configuration {@link lb.kafka.producer.DeliveryType}.
 *
 * @author princearora
 */
public interface Transporter {

   /**
    * Function that will be used by appender to transfer the payload to
    * kafka brokers defined in appender configuration.
    *
    * <p/>
    * returns {@code boolean} to indicate the message delivery status.
    *
    * @param bytes
    *                Payload/ message to be sent to kafka.
    *
    * @param callback
    *                {@link Callback} in case ot message failure.
    *
    * @return
    */
   boolean transport(final byte[] bytes, Callback callback);
}
