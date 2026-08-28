package ch.tbz.m321servicenoah.messaging;

import ch.tbz.m321servicenoah.model.OrderProcessedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessedPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderProcessedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(OrderProcessedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_PROCESSED_EXCHANGE, "", event);
    }
}
