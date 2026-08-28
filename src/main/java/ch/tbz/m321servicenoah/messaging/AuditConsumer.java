package ch.tbz.m321servicenoah.messaging;

import ch.tbz.m321servicenoah.model.OrderProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditConsumer.class);

    @RabbitListener(id = "auditConsumer", queues = RabbitMQConfig.AUDIT_QUEUE)
    public void createAuditEntry(OrderProcessedEvent event) {
        logger.info("Audit entry created for order {}", event.orderId());
    }
}
