package lb.kafka.producer.transport;

import lb.kafka.commons.KafkaHelper;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * Normal Transporter for kafka brokers.
 * {@link lb.kafka.producer.DeliveryType#NORMAL}
 *
 * @author prince.arora
 */
public class NormalTransporter implements Transporter {

    /**
     * kafka producer instance for this transporter.
     */
    private Producer<byte[], byte[]> producer;

    /**
     * Initialize transporter with producer instance.
     *
     * @param producer
     */
    public NormalTransporter(Producer<byte[], byte[]> producer) {
        this.producer = producer;
    }

    /**
     * {@inheritDoc}
     *
     * @param bytes
     *                Payload/ message to be sent to kafka.
     *
     * @param callback
     *                {@link Callback} in case ot message failure.
     *
     * @return
     */
    @Override
    public boolean transport(final byte[] bytes, Callback callback) {
        final Future<RecordMetadata> future = this.producer.send(KafkaHelper.prepareRecord(bytes), callback);
        return future.isDone();
    }

}
