package org.example.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.model.OrderMessage;
import org.example.orderservice.model.dto.OrderRequest;
import org.example.orderservice.service.OrderPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderPublisher orderPublisher;

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Received order request: customerId={}, productId={}",
                request.getCustomerId(), request.getProductId());

        // Convert to message and publish
        OrderMessage orderMessage = OrderMessage.fromRequest(request);
        orderPublisher.publishOrder(orderMessage);

        // Return immediately with 202 Accepted
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderMessage.getOrderId());
        response.put("status", "PENDING");
        response.put("message", "Order received and queued for processing");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is running");
    }
}