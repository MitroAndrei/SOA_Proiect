package org.example.processingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String orderId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String errorMessage;


    public static Order fromMessage(OrderMessage message) {
        return Order.builder()
                .orderId(message.getOrderId())
                .customerId(message.getCustomerId())
                .productId(message.getProductId())
                .quantity(message.getQuantity())
                .price(message.getPrice())
                .totalAmount(message.getTotalAmount())
                .createdAt(message.getCreatedAt())
                .processedAt(LocalDateTime.now())
                .build();
    }


}