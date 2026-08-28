package ch.tbz.m321servicenoah;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ch.tbz.m321servicenoah.controller.OrderController;
import org.springframework.amqp.support.converter.MessageConverter;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.rabbitmq.dynamic=false",
        "messaging.auto-producer.enabled=false",
        "messaging.consumer.processing-time=0"
})
class M321ServiceNoahApplicationTests {
    @Autowired
    private OrderController orderController;

    @Autowired
    private MessageConverter messageConverter;

    @Test
    void contextLoads() {
    }

    @Test
    void orderContractsDescribeTheImplementation() throws Exception {
        String openApi = Files.readString(Path.of("openapi.yaml"));
        String asyncApi = Files.readString(Path.of("asyncapi.yaml"));

        assertThat(orderController).isNotNull();
        assertThat(messageConverter.getClass().getSimpleName()).isEqualTo("JacksonJsonMessageConverter");
        assertThat(openApi).contains("/api/orders:", "application/json:", "'202':");
        assertThat(asyncApi).contains("orders.processing", "orders.processed", "OrderProcessedEvent");
    }

}
