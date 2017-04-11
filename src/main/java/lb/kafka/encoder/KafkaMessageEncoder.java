package lb.kafka.encoder;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

/**
 * Message Encoder for kafka appender. This will be responsible for encoding messages/
 * payload to be sent to kafka brokers. Provided with basic lifecycle methods and
 * with {@link Context}.
 *
 * Implementation for {@link #doEncode(Object)} can be provided based on requirements
 * for encoding.
 *
 * @author princearora
 */
public abstract class KafkaMessageEncoder<E> extends ContextAwareBase implements LifeCycle {

    /**
     * Status indicator.
     */
    protected boolean started;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    /**
     * This will be used by {@link lb.kafka.logback.appender.KafkaAppender} to encode
     * message on every logging event. This method will encode the event message and
     * convert it into byte[].
     *
     * @param event
     * @return
     */
    public abstract byte[] doEncode(E event);
}
