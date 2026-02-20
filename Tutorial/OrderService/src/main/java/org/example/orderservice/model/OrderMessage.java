package org.example.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.model.dto.OrderRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {
    private String orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public static OrderMessage fromRequest(OrderRequest request) {
        return OrderMessage.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalAmount(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .createdAt(LocalDateTime.now())
                .build();
    }
}