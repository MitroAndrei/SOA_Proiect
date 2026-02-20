package org.example.processingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String item;
    private int quantity;
    private double price;
    private String status;
    private LocalDateTime timestamp;

    public OrderCreatedEvent(Order order){
        this.orderId = order.getOrderId();
        this.userId = order.getCustomerId();
        this.item = order.getProductId();
        this.quantity = order.getQuantity();
        this.price = order.getPrice().doubleValue();
        this.status = order.getStatus().name();
        this.timestamp = order.getCreatedAt();
    }
}