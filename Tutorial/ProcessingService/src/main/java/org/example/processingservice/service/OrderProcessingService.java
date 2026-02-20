package org.example.processingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.processingservice.exceptions.InsufficientInventoryException;
import org.example.processingservice.exceptions.PaymentFailedException;
import org.example.processingservice.model.Order;
import org.example.processingservice.model.OrderCreatedEvent;
import org.example.processingservice.model.OrderMessage;
import org.example.processingservice.model.OrderStatus;
import org.example.processingservice.persistence.OrderRepository;
import org.example.processingservice.service.kafka.FaasTriggerPublisher;
import org.example.processingservice.service.kafka.OrderEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final FaasTriggerPublisher faasTriggerPublisher;
    private final LambdaService lambdaService;

    @Transactional
    public void processOrder(OrderMessage message) {
        log.info("Processing order: orderId={}", message.getOrderId());

        // Simulate some business logic processing
        try {
            // Check for duplicate
            if (orderRepository.existsById(message.getOrderId())) {
                log.warn("Duplicate order detected: orderId={}", message.getOrderId());
                return; // Idempotent - skip duplicates
            }

            // Create order entity
            Order order = Order.fromMessage(message);
            order.setStatus(OrderStatus.COMPLETED);

            // Simulate inventory check
            if (!checkInventory(message.getProductId(), message.getQuantity())) {
                throw new InsufficientInventoryException("Product " + message.getProductId() + " out of stock");
            }

            // Simulate payment processing
            if (!processPayment(message.getCustomerId(), message.getTotalAmount())) {
                throw new PaymentFailedException("Payment failed for customer " + message.getCustomerId());
            }

            // Save to database
            orderEventPublisher.publish(new OrderCreatedEvent(orderRepository.save(order)));
            sendEmail(order);
            logOrder(order);


            log.info("Order processed successfully: orderId={}", message.getOrderId());

        } catch (InsufficientInventoryException | PaymentFailedException e) {
            log.error("Business logic error processing order: orderId={}", message.getOrderId(), e);

            // Save failed order
            Order failedOrder = Order.fromMessage(message);
            failedOrder.setStatus(OrderStatus.FAILED);
            failedOrder.setErrorMessage(e.getMessage());

            orderEventPublisher.publish(new OrderCreatedEvent(orderRepository.save(failedOrder)));

        } catch (Exception e) {
            log.error("Unexpected error processing order: orderId={}", message.getOrderId(), e);
            throw new RuntimeException("Failed to process order", e);
        }
    }

    private void logOrder(Order order) {
        String logMessage = String.format("Order Log - ID: %s, Customer: %s, Product: %s, Quantity: %d, Total: $%.2f",
                order.getOrderId(), order.getCustomerId(), order.getProductId(),
                order.getQuantity(), order.getTotalAmount());
        try {
            lambdaService.invokeLogger(logMessage);
        } catch (Exception e) {
            log.error("Failed to invoke logger lambda: {}", e.getMessage());
        }
    }

    private void sendEmail(Order order) {
        faasTriggerPublisher.triggerEmail(
                order.getCustomerId(),
                "Order Confirmation - " + order.getOrderId(),
                "Your order has been processed successfully. Total: $" + order.getTotalAmount()
        );
    }

    private boolean checkInventory(String productId, int quantity) {
        // Simulate inventory check
        // In real app, this would call an inventory service
        log.debug("Checking inventory for product: {}, quantity: {}", productId, quantity);

        // Simulate occasional out of stock (10% failure rate)
        return Math.random() > 0.1;
    }

    private boolean processPayment(String customerId, BigDecimal amount) {
        // Simulate payment processing
        // In real app, this would call a payment gateway
        log.debug("Processing payment for customer: {}, amount: {}", customerId, amount);

        // Simulate occasional payment failures (5% failure rate)
        return Math.random() > 0.05;
    }
}