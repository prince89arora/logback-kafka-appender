package lb.kafka.producer.transport;

import lb.kafka.commons.KafkaHelper;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * Implementation for YieldTransporter, for every delivery completion
 * will be checked and status will be returned.
 * {@link lb.kafka.producer.DeliveryType#YIElD}
 *
 * @author princearora
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
     */
    public YieldTransporter(Producer<byte[], byte[]> producer) {
        this.producer = producer;
    }

    /**
     * {@inheritDoc}
     *
     * @param bytes
     *                Payload/ message to be sent to kafka.
     *
     * @param callback
     *                {@link Callback} in case of message failure.
     *
     * @return
     */
    @Override
    public boolean transport(byte[] bytes, Callback callback) {
        //TODO: Make use of callback
        final Future<RecordMetadata> future = this.producer.send(KafkaHelper.prepareRecord(bytes));

        while (!future.isDone()) {
            //wait for it to complete
        }
        return future.isDone();
    }
}
