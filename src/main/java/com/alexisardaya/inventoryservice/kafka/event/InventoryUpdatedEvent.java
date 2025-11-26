package com.alexisardaya.inventoryservice.kafka.event;

import java.time.Instant;

public class InventoryUpdatedEvent {

  private Long productId;
  private Integer availableStock;
  private Integer reservedStock;
  private Integer totalStock;
  private Instant timestamp;

  public InventoryUpdatedEvent() {
  }

  public InventoryUpdatedEvent(Long productId, Integer availableStock,
      Integer reservedStock, Integer totalStock) {
    this.productId = productId;
    this.availableStock = availableStock;
    this.reservedStock = reservedStock;
    this.totalStock = totalStock;
    this.timestamp = Instant.now();
  }

  // Getters y Setters
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getAvailableStock() {
    return availableStock;
  }

  public void setAvailableStock(Integer availableStock) {
    this.availableStock = availableStock;
  }

  public Integer getReservedStock() {
    return reservedStock;
  }

  public void setReservedStock(Integer reservedStock) {
    this.reservedStock = reservedStock;
  }

  public Integer getTotalStock() {
    return totalStock;
  }

  public void setTotalStock(Integer totalStock) {
    this.totalStock = totalStock;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }
}
