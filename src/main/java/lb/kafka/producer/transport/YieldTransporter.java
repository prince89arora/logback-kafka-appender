package lb.kafka.producer.transport;

import lb.kafka.commons.KafkaHelper;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
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

    @Override
    public boolean transport(byte[] bytes, Callback callback) {
        //TODO: Make use of callback
        boolean status = true;
        final Future<RecordMetadata> future = this.producer.send(KafkaHelper.prepareRecord(bytes));

        while (!future.isDone()) {
            //wait for it to complete
        }
        return future.isDone();
    }
}
