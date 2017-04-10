package lb.kafka.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Implementation for Kafka Message encoder for pattern based layout encoding.
 *
 * @author princearora
 */
public class PatternBasedMessageEncoder<E> extends KafkaMessageEncoder<E> {

    /**
     * pattern provided in appender configuration.
     */
    private String pattern;

    /**
     * Pattern layout
     */
    private PatternLayout layout;

    /**
     * Default pattern is no pattern provided in configuration.
     */
    private static final String DEFAULT_PATTER = "%d{yyyy-MM-dd HH:mm:ss} - %msg%n";

    @Override
    public void start() {
        this.started = true;
        this.layout = new PatternLayout();
        this.layout.setContext(this.getContext());

        if (this.pattern == null || this.pattern.equals("")) {
            this.layout.setPattern(DEFAULT_PATTER);
            return;
        }
        this.layout.setPattern(this.getPattern());
    }

    @Override
    public void stop() {
        this.started = false;
        this.layout = null;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     *
     * @param event
     * @return
     */
    @Override
    public byte[] doEncode(E event) {
        return this.layout.doLayout( (ILoggingEvent) event ).getBytes();
    }
}
