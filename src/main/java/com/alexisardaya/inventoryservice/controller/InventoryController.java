package com.alexisardaya.inventoryservice.controller;

import com.alexisardaya.inventoryservice.dto.InventoryItemRequest;
import com.alexisardaya.inventoryservice.dto.InventoryItemResponse;
import com.alexisardaya.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

  private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

  private final InventoryService inventoryService;

  // Constructor injection
  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  /**
   * POST /api/inventory - Crear item de inventario
   */
  @PostMapping
  public ResponseEntity<InventoryItemResponse> createInventoryItem(
      @Valid @RequestBody InventoryItemRequest request) {
    log.info("POST /api/inventory - Creating inventory item: {}", request);
    InventoryItemResponse response = inventoryService.createInventoryItem(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * GET /api/inventory - Listar todos los items
   */
  @GetMapping
  public ResponseEntity<List<InventoryItemResponse>> getAllInventoryItems() {
    log.debug("GET /api/inventory - Fetching all inventory items");
    List<InventoryItemResponse> items = inventoryService.getAllInventoryItems();
    return ResponseEntity.ok(items);
  }

  /**
   * GET /api/inventory/{id} - Obtener item por ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<InventoryItemResponse> getInventoryItemById(@PathVariable Long id) {
    log.debug("GET /api/inventory/{} - Fetching inventory item", id);
    InventoryItemResponse response = inventoryService.getInventoryItemById(id);
    return ResponseEntity.ok(response);
  }

  /**
   * GET /api/inventory/product/{productId} - Obtener item por productId
   */
  @GetMapping("/product/{productId}")
  public ResponseEntity<InventoryItemResponse> getInventoryItemByProductId(
      @PathVariable Long productId) {
    log.debug("GET /api/inventory/product/{} - Fetching inventory item", productId);
    InventoryItemResponse response = inventoryService.getInventoryItemByProductId(productId);
    return ResponseEntity.ok(response);
  }

  /**
   * DELETE /api/inventory/{id} - Eliminar item
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
    log.info("DELETE /api/inventory/{} - Deleting inventory item", id);
    inventoryService.deleteInventoryItem(id);
    return ResponseEntity.noContent().build();
  }
}
