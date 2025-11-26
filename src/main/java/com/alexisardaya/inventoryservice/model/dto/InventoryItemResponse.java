package com.alexisardaya.inventoryservice.model.dto;

import java.time.Instant;

public class InventoryItemResponse {

  private Long id;
  private Long productId;
  private String productName;
  private Integer availableStock;
  private Integer reservedStock;
  private Integer totalStock;
  private Instant createdAt;
  private Instant updatedAt;

  // Constructor vacío
  public InventoryItemResponse() {
  }

  // Constructor completo
  public InventoryItemResponse(Long id, Long productId, String productName,
      Integer availableStock, Integer reservedStock,
      Integer totalStock, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.availableStock = availableStock;
    this.reservedStock = reservedStock;
    this.totalStock = totalStock;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Getters y Setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "InventoryItemResponse{" +
        "id=" + id +
        ", productId=" + productId +
        ", productName='" + productName + '\'' +
        ", availableStock=" + availableStock +
        ", reservedStock=" + reservedStock +
        ", totalStock=" + totalStock +
        '}';
  }
}
