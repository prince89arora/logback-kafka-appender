package lb.kafka.test;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lb.kafka.encoder.PatternBasedMessageEncoder;
import lb.kafka.logback.appender.KafkaAppender;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 * Kafka Appender test cases.
 *
 * @author prince.arora
 */
public class KafkaAppenderTest {


    /**
     * Kafka appender test for xml configurations
     */
    @Test
    public void testXmlConfig() {
        boolean status = false;
        try {
            TestApplication.info();
            status = true;
        } catch (Exception e) {}
        assertTrue(status);
    }

    /**
     * Kafka appender test for programmable kafka appender.
     */
    @Test
    public void programmableAppenderTest() {
        boolean status = false;
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getILoggerFactory().getLogger("TestLogger");
            KafkaAppender<ILoggingEvent> kafkaAppender = new KafkaAppender();
            kafkaAppender.setBrokers("192.168.1.167:9092");

            PatternBasedMessageEncoder messageEncoder = new PatternBasedMessageEncoder();
            messageEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
            messageEncoder.setContext(loggerContext);
            messageEncoder.start();

            kafkaAppender.setEncoder(messageEncoder);
            kafkaAppender.setTopic("test");
            kafkaAppender.start();


            logger.addAppender(kafkaAppender);
            logger.info("Test info from programmable appender..");
            status = true;
        } catch (Exception e) {}
        assertTrue(status);
    }

    public static class TestApplication {

        private static final Logger LOGGER = LoggerFactory.getLogger(TestApplication.class);

        public static void info() {
            LOGGER.info("log test from xml configuration !!");
        }
    }
}
