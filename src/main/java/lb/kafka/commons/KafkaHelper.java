package lb.kafka.commons;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Helper class to perform utility tasks.
 *
 * @author prince.arora
 */
public final class KafkaHelper {

    /**
     * Prepare {@link ProducerRecord} for transportation.
     *
     * @param payload
     *                  byte array form of message.
     * @return
     *                  {@link ProducerRecord}
     */
    public static ProducerRecord<byte[], byte[]> prepareRecord(byte[] payload) {
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(
                ModuleAware.CONTEXT.getTopic(),
                payload
        );
        return record;
    }

    /**
     * Prepare {@link ProducerRecord} for transportation.
     *
     * @param payload
     *                  byte array form of message.
     * @param key
     *                  key to be used on kafka.
     * @return
     *                  {@link ProducerRecord}
     */
    public static ProducerRecord<byte[], byte[]> prepareRecord(byte[] payload, byte[] key) {
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(
                ModuleAware.CONTEXT.getTopic(),
                key,
                payload
        );
        return record;
    }

}
