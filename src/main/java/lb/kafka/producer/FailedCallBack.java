package lb.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Default Failed call back implementation.
 *
 * @author prince.arora
 */
public class FailedCallBack implements Callback {

    /**
     * Default action performed on failed transportation.
     *
     * @param recordMetadata
     *                          {@link RecordMetadata}
     * @param e
     *                          {@link Exception}
     */
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        StringBuilder builder = new StringBuilder();
        builder.append("Unable to transport data to kafka -> ")
                .append("Topic : ").append(recordMetadata.topic())
                .append("Time : ").append(recordMetadata.timestamp());
        System.out.println(builder);
    }
}
