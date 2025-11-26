package com.alexisardaya.inventoryservice.exception;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static ResourceNotFoundException inventoryItem(Long id) {
    return new ResourceNotFoundException("Item de inventario " + id + " no encontrado");
  }

  public static ResourceNotFoundException inventoryItemByProductId(Long productId) {
    return new ResourceNotFoundException("Item de inventario no encontrado para productId: " + productId);
  }
}

