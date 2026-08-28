package ch.tbz.m321servicenoah.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String ORDER_PROCESSING_QUEUE = "orders.processing";
    public static final String ORDER_PROCESSED_EXCHANGE = "orders.processed";
    public static final String NOTIFICATION_QUEUE = "orders.notification";
    public static final String AUDIT_QUEUE = "orders.audit";

    @Bean
    Queue orderProcessingQueue() {
        return new Queue(ORDER_PROCESSING_QUEUE, true);
    }

    @Bean
    FanoutExchange orderProcessedExchange() {
        return new FanoutExchange(ORDER_PROCESSED_EXCHANGE, true, false);
    }

    @Bean
    Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    Queue auditQueue() {
        return new Queue(AUDIT_QUEUE, true);
    }

    @Bean
    Binding notificationBinding(FanoutExchange orderProcessedExchange) {
        return BindingBuilder.bind(notificationQueue()).to(orderProcessedExchange);
    }

    @Bean
    Binding auditBinding(FanoutExchange orderProcessedExchange) {
        return BindingBuilder.bind(auditQueue()).to(orderProcessedExchange);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
