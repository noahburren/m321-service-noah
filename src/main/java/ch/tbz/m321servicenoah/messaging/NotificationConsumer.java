package ch.tbz.m321servicenoah.messaging;

import ch.tbz.m321servicenoah.model.OrderProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(id = "notificationConsumer", queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void notifyCustomer(OrderProcessedEvent event) {
        logger.info("Notification sent for order {}", event.orderId());
    }
}
