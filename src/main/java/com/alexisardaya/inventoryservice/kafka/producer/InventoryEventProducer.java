package com.alexisardaya.inventoryservice.kafka.producer;

import com.alexisardaya.inventoryservice.kafka.event.InventoryUpdatedEvent;
import com.alexisardaya.inventoryservice.kafka.event.OrderCancelledEvent;
import com.alexisardaya.inventoryservice.kafka.event.OrderConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer de eventos de inventario. Publica OrderConfirmedEvent y OrderCancelledEvent a Kafka.
 */
@Component
public class InventoryEventProducer {

  private static final Logger log = LoggerFactory.getLogger(InventoryEventProducer.class);

  private static final String TOPIC_ORDERS_CONFIRMED = "ecommerce.orders.confirmed";
  private static final String TOPIC_ORDERS_CANCELLED = "ecommerce.orders.cancelled";
  private static final String TOPIC_INVENTORY_UPDATED = "ecommerce.inventory.updated";

  private final KafkaTemplate<String, OrderConfirmedEvent> confirmedKafkaTemplate;
  private final KafkaTemplate<String, OrderCancelledEvent> cancelledKafkaTemplate;
  private final KafkaTemplate<String, InventoryUpdatedEvent> inventoryUpdatedKafkaTemplate;

  // Constructor injection
  public InventoryEventProducer(
      KafkaTemplate<String, OrderConfirmedEvent> confirmedKafkaTemplate,
      KafkaTemplate<String, OrderCancelledEvent> cancelledKafkaTemplate,
      KafkaTemplate<String, InventoryUpdatedEvent> inventoryUpdatedKafkaTemplate) {
    this.confirmedKafkaTemplate = confirmedKafkaTemplate;
    this.cancelledKafkaTemplate = cancelledKafkaTemplate;
    this.inventoryUpdatedKafkaTemplate = inventoryUpdatedKafkaTemplate;
  }

  /**
   * Publica OrderConfirmedEvent cuando el stock fue reservado exitosamente
   */
  public void publishOrderConfirmed(OrderConfirmedEvent event) {
    log.info("Publishing OrderConfirmedEvent: orderId={}, productId={}, quantity={}",
        event.getOrderId(), event.getProductId(), event.getQuantity());

    String key = event.getOrderId().toString();

    confirmedKafkaTemplate.send(TOPIC_ORDERS_CONFIRMED, key, event)
        .whenComplete((result, ex) -> {
          if (ex != null) {
            log.error("Failed to publish OrderConfirmedEvent: orderId={}, error={}",
                event.getOrderId(), ex.getMessage(), ex);
          } else {
            log.info(
                "OrderConfirmedEvent published successfully: orderId={}, partition={}, offset={}",
                event.getOrderId(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
          }
        });
  }

  /**
   * Publica OrderCancelledEvent cuando no hay stock suficiente
   */
  public void publishOrderCancelled(OrderCancelledEvent event) {
    log.info("Publishing OrderCancelledEvent: orderId={}, productId={}, reason={}",
        event.getOrderId(), event.getProductId(), event.getReason());

    String key = event.getOrderId().toString();

    cancelledKafkaTemplate.send(TOPIC_ORDERS_CANCELLED, key, event)
        .whenComplete((result, ex) -> {
          if (ex != null) {
            log.error("Failed to publish OrderCancelledEvent: orderId={}, error={}",
                event.getOrderId(), ex.getMessage(), ex);
          } else {
            log.info(
                "OrderCancelledEvent published successfully: orderId={}, partition={}, offset={}",
                event.getOrderId(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
          }
        });
  }

  public void publishInventoryUpdated(InventoryUpdatedEvent event) {
    log.info("Publishing InventoryUpdatedEvent: productId={}, availableStock={}",
        event.getProductId(), event.getAvailableStock());

    inventoryUpdatedKafkaTemplate.send(TOPIC_INVENTORY_UPDATED,
        event.getProductId().toString(),
        event);
  }
}
