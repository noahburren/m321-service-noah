package ch.tbz.m321servicenoah.messaging;

import java.time.Instant;

import ch.tbz.m321servicenoah.model.OrderMessage;
import ch.tbz.m321servicenoah.model.OrderProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderWorkers {

    private static final Logger logger = LoggerFactory.getLogger(OrderWorkers.class);

    private final OrderProcessedPublisher processedPublisher;
    private final long processingTime;

    public OrderWorkers(
            OrderProcessedPublisher processedPublisher,
            @Value("${messaging.consumer.processing-time}") long processingTime) {
        this.processedPublisher = processedPublisher;
        this.processingTime = processingTime;
    }

    @RabbitListener(id = "orderWorker1", queues = RabbitMQConfig.ORDER_PROCESSING_QUEUE)
    public void processWithWorker1(OrderMessage order) throws InterruptedException {
        process("OrderWorker1", order);
    }

    @RabbitListener(id = "orderWorker2", queues = RabbitMQConfig.ORDER_PROCESSING_QUEUE)
    public void processWithWorker2(OrderMessage order) throws InterruptedException {
        process("OrderWorker2", order);
    }

    private void process(String workerName, OrderMessage order) throws InterruptedException {
        logger.info("{} started processing order {}", workerName, order.orderId());
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw exception;
        }

        logger.info("{} finished processing order {}", workerName, order.orderId());
        processedPublisher.publish(new OrderProcessedEvent(
                order.orderId(), order.product(), order.quantity(), Instant.now(), workerName));
    }
}
