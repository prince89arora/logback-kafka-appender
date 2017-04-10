package lb.kafka.encoder;

import ch.qos.logback.core.Context;
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
public abstract class KafkaMessageEncoder<E> implements LifeCycle {

    /**
     * Logger context
     */
    protected Context context;

    /**
     * Status indicator.
     */
    protected boolean started;

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    @Override
    public boolean isStarted() {
        return this.started;
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

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
