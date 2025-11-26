package com.alexisardaya.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InventoryItemRequest {

  @NotNull(message = "{inventory.productId.notnull}")
  private Long productId;

  @NotBlank(message = "{inventory.productName.notblank}")
  private String productName;

  @NotNull(message = "{inventory.initialStock.notnull}")
  @Min(value = 0, message = "{inventory.initialStock.min}")
  private Integer initialStock;

  // Constructor vacío
  public InventoryItemRequest() {
  }

  // Constructor completo
  public InventoryItemRequest(Long productId, String productName, Integer initialStock) {
    this.productId = productId;
    this.productName = productName;
    this.initialStock = initialStock;
  }

  // Getters y Setters

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

  public Integer getInitialStock() {
    return initialStock;
  }

  public void setInitialStock(Integer initialStock) {
    this.initialStock = initialStock;
  }

  @Override
  public String toString() {
    return "InventoryItemRequest{" +
        "productId=" + productId +
        ", productName='" + productName + '\'' +
        ", initialStock=" + initialStock +
        '}';
  }
}

