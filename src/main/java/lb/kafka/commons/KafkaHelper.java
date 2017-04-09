package lb.kafka.commons;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Helper class to perform utility tasks.
 *
 * @author princearora
 */
public final class KafkaHelper {

    /**
     * Prepare {@link ProducerRecord<byte[], byte[]>}
     *
     * @param payload
     * @return
     */
    public static ProducerRecord<byte[], byte[]> prepareRecord(byte[] payload) {
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(
                ModuleAware.CONTEXT.getTopic(),
                payload
        );
        return record;
    }

    /**
     * Prepare {@link ProducerRecord<byte[], byte[]>}
     *
     * @param payload
     * @param key
     * @return
     */
    public static ProducerRecord<byte[], byte[]> prepareRecord(byte[] payload, byte[] key) {
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(
                ModuleAware.CONTEXT.getTopic(),
                key,
                payload
        );
        return record;
    }

    /**
     * Prepare final configuration map for producer to initiate kafka producer instance
     * Configurations will be picked up from {@link ModuleAware}
     *
     * @return
     */
    public static Map<String, Object> prepareConfiguration() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, ModuleAware.CONTEXT.getBrokers());
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(BATCH_SIZE_CONFIG, 0);
        return properties;
    }
}
