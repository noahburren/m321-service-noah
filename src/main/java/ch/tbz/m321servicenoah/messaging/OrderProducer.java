package ch.tbz.m321servicenoah.messaging;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import ch.tbz.m321servicenoah.model.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final AtomicLong nextOrderId = new AtomicLong(100);

    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public OrderMessage createOrder(String product, int quantity) {
        OrderMessage order = new OrderMessage(
                nextOrderId.incrementAndGet(), product, quantity, Instant.now());
        rabbitTemplate.convertAndSend("", RabbitMQConfig.ORDER_PROCESSING_QUEUE, order);
        logger.info("Order {} accepted: {} x{}", order.orderId(), order.product(), order.quantity());
        return order;
    }
}
