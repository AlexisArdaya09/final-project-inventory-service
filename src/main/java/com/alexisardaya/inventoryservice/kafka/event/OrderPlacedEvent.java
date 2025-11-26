package com.alexisardaya.inventoryservice.kafka.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Evento recibido cuando se crea una nueva orden en order-service. IMPORTANTE: Esta clase es una
 * COPIA del evento de order-service. En arquitecturas de microservicios, cada servicio tiene su
 * propia copia de los eventos que consume (no hay librería compartida).
 */
public class OrderPlacedEvent {

  private Long orderId;
  private Long productId;
  private Integer quantity;
  private String customerName;
  private String customerEmail;
  private BigDecimal totalAmount;
  private Instant timestamp;

  // Constructor vacío (REQUERIDO para deserialización JSON)
  public OrderPlacedEvent() {
  }

  // Constructor completo
  public OrderPlacedEvent(Long orderId, Long productId, Integer quantity,
      String customerName, String customerEmail,
      BigDecimal totalAmount, Instant timestamp) {
    this.orderId = orderId;
    this.productId = productId;
    this.quantity = quantity;
    this.customerName = customerName;
    this.customerEmail = customerEmail;
    this.totalAmount = totalAmount;
    this.timestamp = timestamp;
  }

  // Getters y Setters

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "OrderPlacedEvent{" +
        "orderId=" + orderId +
        ", productId=" + productId +
        ", quantity=" + quantity +
        ", customerName='" + customerName + '\'' +
        ", customerEmail='" + customerEmail + '\'' +
        ", totalAmount=" + totalAmount +
        ", timestamp=" + timestamp +
        '}';
  }
}
