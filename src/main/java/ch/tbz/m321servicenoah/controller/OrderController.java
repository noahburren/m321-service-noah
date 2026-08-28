package ch.tbz.m321servicenoah.controller;

import ch.tbz.m321servicenoah.messaging.OrderProducer;
import ch.tbz.m321servicenoah.model.CreateOrderRequest;
import ch.tbz.m321servicenoah.model.OrderMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<OrderMessage> createOrder(@RequestBody CreateOrderRequest request) {
        if (request.product() == null || request.product().isBlank() || request.quantity() < 1) {
            throw new ResponseStatusException(BAD_REQUEST, "product is required and quantity must be positive");
        }
        OrderMessage order = orderProducer.createOrder(request.product(), request.quantity());
        return ResponseEntity.accepted().body(order);
    }
}
