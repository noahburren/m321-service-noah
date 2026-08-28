package ch.tbz.m321servicenoah.controller;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.context.Lifecycle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    private final RabbitListenerEndpointRegistry listenerRegistry;

    public ConsumerController(RabbitListenerEndpointRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopConsumers() {
        listenerRegistry.getListenerContainers().forEach(Lifecycle::stop);
        return ResponseEntity.ok("All RabbitMQ consumers stopped");
    }

    @PostMapping("/start")
    public ResponseEntity<String> startConsumers() {
        listenerRegistry.getListenerContainers().forEach(Lifecycle::start);
        return ResponseEntity.ok("All RabbitMQ consumers started");
    }
}
