package lb.kafka.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Implementation for Kafka Message encoder for pattern based layout encoding.
 *
 * @author prince.arora
 */
public class PatternBasedMessageEncoder<E> extends KafkaMessageEncoder<E> {

    /**
     * pattern provided in appender configuration.
     */
    private String pattern;

    /**
     * Pattern layout
     */
    private static PatternLayout layout;

    /**
     * Default pattern is no pattern provided in configuration.
     */
    private static final String DEFAULT_PATTER = "%d{yyyy-MM-dd HH:mm:ss} %-5level - %msg%n";

    @Override
    public void start() {
        started = true;
        layout = new PatternLayout();
        layout.setContext(this.getContext());

        if (this.pattern == null || this.pattern.equals("")) {
            layout.setPattern(DEFAULT_PATTER);
            layout.start();
            return;
        }
        layout.setPattern(this.getPattern());
        layout.start();
    }

    @Override
    public void stop() {
        started = false;
        layout = null;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] doEncode(E event) {
        return layout.doLayout( (ILoggingEvent) event ).getBytes();
    }
}
