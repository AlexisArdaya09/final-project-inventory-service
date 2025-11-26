package com.alexisardaya.inventoryservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long productId;  // FK lógico a product-service

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private Integer availableStock;

  @Column(nullable = false)
  private Integer reservedStock;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  // Constructor vacío (requerido por JPA)
  public InventoryItem() {
  }

  // Constructor completo
  public InventoryItem(Long productId, String productName, Integer initialStock) {
    this.productId = productId;
    this.productName = productName;
    this.availableStock = initialStock;
    this.reservedStock = 0;
  }

  // Métodos de negocio

  /**
   * Verifica si hay stock disponible suficiente
   */
  public boolean hasAvailableStock(Integer quantity) {
    return this.availableStock >= quantity;
  }

  /**
   * Reserva stock (mueve de disponible a reservado) NO persiste automáticamente, debe llamar
   * repository.save()
   */
  public void reserveStock(Integer quantity) {
    if (!hasAvailableStock(quantity)) {
      throw new IllegalStateException(
          "Insufficient available stock. Available: " + availableStock + ", Requested: " + quantity
      );
    }
    this.availableStock -= quantity;
    this.reservedStock += quantity;
  }

  /**
   * Libera stock (mueve de reservado a disponible) Usado cuando una orden se cancela
   */
  public void releaseStock(Integer quantity) {
    if (this.reservedStock < quantity) {
      throw new IllegalStateException(
          "Cannot release more than reserved. Reserved: " + reservedStock + ", Requested: "
              + quantity
      );
    }
    this.reservedStock -= quantity;
    this.availableStock += quantity;
  }

  /**
   * Calcula el stock total (disponible + reservado)
   */
  public Integer getTotalStock() {
    return this.availableStock + this.reservedStock;
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
    return "InventoryItem{" +
        "id=" + id +
        ", productId=" + productId +
        ", productName='" + productName + '\'' +
        ", availableStock=" + availableStock +
        ", reservedStock=" + reservedStock +
        ", totalStock=" + getTotalStock() +
        '}';
  }
}

