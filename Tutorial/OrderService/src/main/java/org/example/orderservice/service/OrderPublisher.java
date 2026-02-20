package org.example.orderservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.model.OrderMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publishOrder(OrderMessage orderMessage) {
        try {
            log.info("Publishing order to queue: orderId={}", orderMessage.getOrderId());

            rabbitTemplate.convertAndSend(exchange, routingKey, orderMessage);

            log.info("Order published successfully: orderId={}", orderMessage.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish order: orderId={}", orderMessage.getOrderId(), e);
            throw new RuntimeException("Failed to publish order to message queue", e);
        }
    }
}