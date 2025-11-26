package com.alexisardaya.inventoryservice.exception;

public class InventoryItemAlreadyExistsException extends RuntimeException {

  public InventoryItemAlreadyExistsException(String message) {
    super(message);
  }

  public InventoryItemAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public static InventoryItemAlreadyExistsException forProductId(Long productId) {
    return new InventoryItemAlreadyExistsException(
        "Ya existe un item de inventario para el productId: " + productId
    );
  }
}

