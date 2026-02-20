package org.example.processingservice.service.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.processingservice.model.OrderMessage;
import org.example.processingservice.service.OrderProcessingService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessageListener {

    private final OrderProcessingService orderProcessingService;

    @RabbitListener(queues = "${rabbitmq.queue}", ackMode = "MANUAL")
    public void handleOrderMessage(
            @Payload OrderMessage orderMessage,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            Message message) {

        log.info("Received order message: orderId={}", orderMessage.getOrderId());

        try {
            // Process the order
            orderProcessingService.processOrder(orderMessage);

            // Manually acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Message acknowledged: orderId={}", orderMessage.getOrderId());

        } catch (Exception e) {
            log.error("Error processing order message: orderId={}", orderMessage.getOrderId(), e);

            try {
                // Check if this is a redelivered message
                Boolean redelivered = message.getMessageProperties().getRedelivered();

                if (Boolean.TRUE.equals(redelivered)) {
                    // Message has been redelivered, send to DLQ
                    log.error("Message redelivered, rejecting to DLQ: orderId={}", orderMessage.getOrderId());
                    channel.basicReject(deliveryTag, false); // false = don't requeue
                } else {
                    // First failure, requeue for retry
                    log.warn("First failure, requeuing message: orderId={}", orderMessage.getOrderId());
                    channel.basicNack(deliveryTag, false, true); // true = requeue
                }

            } catch (IOException ioException) {
                log.error("Failed to reject/nack message", ioException);
            }
        }
    }
}