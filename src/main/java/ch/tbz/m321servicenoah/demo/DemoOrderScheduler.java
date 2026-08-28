package ch.tbz.m321servicenoah.demo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ch.tbz.m321servicenoah.messaging.OrderProducer;
import ch.tbz.m321servicenoah.model.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "messaging.auto-producer.enabled", havingValue = "true")
public class DemoOrderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DemoOrderScheduler.class);
    private static final List<String> PRODUCTS = List.of(
            "Keyboard", "Mouse", "Monitor", "Headset", "SSD");

    private final OrderProducer orderProducer;
    private final AtomicInteger nextProduct = new AtomicInteger();

    public DemoOrderScheduler(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @Scheduled(fixedRateString = "${messaging.auto-producer.interval}")
    public void createDemoOrder() {
        int index = nextProduct.getAndIncrement();
        String product = PRODUCTS.get(index % PRODUCTS.size());
        int quantity = index % 3 + 1;
        OrderMessage order = orderProducer.createOrder(product, quantity);
        logger.info("Created demo order #{}: {} x{}",
                order.orderId(), order.product(), order.quantity());
    }
}
