package lb.kafka.producer.transport;

import lb.kafka.commons.KafkaHelper;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * <p>
 * Implementation for YieldTransporter, for every delivery completion
 * will be checked and status will be returned.
 * {@link lb.kafka.producer.DeliveryType#YIElD}
 * </p>
 *
 * @author prince.arora
 */
public class YieldTransporter implements Transporter {

    /**
     * kafka producer instance for this transporter.
     */
    private Producer<byte[], byte[]> producer;

    /**
     * Initialize by providing producer instance.
     *
     * @param producer
     *             kafka producer.
     */
    public YieldTransporter(Producer<byte[], byte[]> producer) {
        this.producer = producer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transport(byte[] bytes, Callback callback) {
        final Future<RecordMetadata> future = this.producer.send(KafkaHelper.prepareRecord(bytes), callback);

        while (!future.isDone()) {
            //wait for it to complete
        }
        return future.isDone();
    }
}
