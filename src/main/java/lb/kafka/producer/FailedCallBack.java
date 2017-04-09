package lb.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * @author princearora
 */
public class FailedCallBack implements Callback {

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        //TODO: things to do for failure
    }
}
