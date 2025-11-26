package com.alexisardaya.inventoryservice.service;

import com.alexisardaya.inventoryservice.kafka.event.InventoryUpdatedEvent;
import com.alexisardaya.inventoryservice.kafka.event.OrderCancelledEvent;
import com.alexisardaya.inventoryservice.kafka.event.OrderConfirmedEvent;
import com.alexisardaya.inventoryservice.kafka.producer.InventoryEventProducer;
import com.alexisardaya.inventoryservice.model.dto.InventoryItemRequest;
import com.alexisardaya.inventoryservice.model.dto.InventoryItemResponse;
import com.alexisardaya.inventoryservice.model.entity.InventoryItem;
import com.alexisardaya.inventoryservice.repository.InventoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

  private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

  private final InventoryRepository inventoryRepository;
  private final InventoryEventProducer eventProducer;

  // Constructor injection
  public InventoryService(InventoryRepository inventoryRepository,
      InventoryEventProducer eventProducer) {
    this.inventoryRepository = inventoryRepository;
    this.eventProducer = eventProducer;
  }

  /**
   * Crear un nuevo item de inventario
   */
  @Transactional
  public InventoryItemResponse createInventoryItem(InventoryItemRequest request) {
    log.info("Creating inventory item for productId: {}", request.getProductId());

    // Verificar que no exista ya un item para este producto
    if (inventoryRepository.existsByProductId(request.getProductId())) {
      throw new RuntimeException(
          "Inventory item already exists for productId: " + request.getProductId());
    }

    InventoryItem item = new InventoryItem(
        request.getProductId(),
        request.getProductName(),
        request.getInitialStock()
    );

    InventoryItem saved = inventoryRepository.save(item);
    log.info("Inventory item created: {}", saved);

    return toResponse(saved);
  }

  /**
   * Obtener todos los items de inventario
   */
  @Transactional(readOnly = true)
  public List<InventoryItemResponse> getAllInventoryItems() {
    log.debug("Fetching all inventory items");
    return inventoryRepository.findAll()
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Obtener item por ID
   */
  @Transactional(readOnly = true)
  public InventoryItemResponse getInventoryItemById(Long id) {
    log.debug("Fetching inventory item by id: {}", id);
    InventoryItem item = inventoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));
    return toResponse(item);
  }

  /**
   * Obtener item por productId
   */
  @Transactional(readOnly = true)
  public InventoryItemResponse getInventoryItemByProductId(Long productId) {
    log.debug("Fetching inventory item by productId: {}", productId);
    InventoryItem item = inventoryRepository.findByProductId(productId)
        .orElseThrow(
            () -> new RuntimeException("Inventory item not found for productId: " + productId));
    return toResponse(item);
  }

  /**
   * Eliminar item de inventario
   */
  @Transactional
  public void deleteInventoryItem(Long id) {
    log.info("Deleting inventory item with id: {}", id);
    if (!inventoryRepository.existsById(id)) {
      throw new RuntimeException("Inventory item not found with id: " + id);
    }
    inventoryRepository.deleteById(id);
    log.info("Inventory item deleted: id={}", id);
  }

  /**
   * Procesa un evento OrderPlacedEvent: 1. Busca el item de inventario por productId 2. Valida que
   * haya stock disponible 3. SI HAY STOCK: Reserva y publica OrderConfirmedEvent 4. SI NO HAY
   * STOCK: Publica OrderCancelledEvent
   */
  @Transactional
  public void processOrderPlaced(
      com.alexisardaya.inventoryservice.kafka.event.OrderPlacedEvent event) {
    log.info("Processing order: orderId={}, productId={}, quantity={}",
        event.getOrderId(), event.getProductId(), event.getQuantity());

    try {
      // 1. Buscar item de inventario
      InventoryItem item = inventoryRepository.findByProductId(event.getProductId())
          .orElseThrow(() -> new RuntimeException(
              "Inventory item not found for productId: " + event.getProductId()
          ));

      log.debug("Current stock before reservation: availableStock={}, reservedStock={}",
          item.getAvailableStock(), item.getReservedStock());

      // 2. Validar stock disponible
      if (!item.hasAvailableStock(event.getQuantity())) {
        log.warn(
            "Insufficient stock for order: orderId={}, productId={}, requested={}, available={}",
            event.getOrderId(), event.getProductId(), event.getQuantity(),
            item.getAvailableStock());

        // Publicar OrderCancelledEvent
        OrderCancelledEvent cancelledEvent = new OrderCancelledEvent(
            event.getOrderId(),
            event.getProductId(),
            event.getQuantity(),
            item.getAvailableStock(),
            "Insufficient stock"
        );
        eventProducer.publishOrderCancelled(cancelledEvent);

        return; // NO lanzar excepción, solo publicar evento de cancelación
      }

      // 3. Reservar stock (usa método de negocio de la entidad)
      item.reserveStock(event.getQuantity());

      // 4. Persistir cambios
      inventoryRepository.save(item);

      log.info(
          "Stock reserved successfully: orderId={}, productId={}, newAvailableStock={}, newReservedStock={}",
          event.getOrderId(), event.getProductId(), item.getAvailableStock(),
          item.getReservedStock());

      // 5. Publicar OrderConfirmedEvent
      OrderConfirmedEvent confirmedEvent = new OrderConfirmedEvent(
          event.getOrderId(),
          event.getProductId(),
          event.getQuantity(),
          item.getAvailableStock(),
          item.getReservedStock()
      );
      eventProducer.publishOrderConfirmed(confirmedEvent);

      // 6. Publicar InventoryUpdatedEvent (NUEVO)
      InventoryUpdatedEvent inventoryEvent = new InventoryUpdatedEvent(
          item.getProductId(),
          item.getAvailableStock(),
          item.getReservedStock(),
          item.getTotalStock()
      );
      eventProducer.publishInventoryUpdated(inventoryEvent);
    } catch (Exception e) {
      log.error("Error processing order: orderId={}, error={}", event.getOrderId(), e.getMessage(),
          e);

      // Publicar OrderCancelledEvent en caso de error inesperado
      OrderCancelledEvent cancelledEvent = new OrderCancelledEvent(
          event.getOrderId(),
          event.getProductId(),
          event.getQuantity(),
          0, // No tenemos el stock disponible en caso de error
          "Error processing order: " + e.getMessage()
      );
      eventProducer.publishOrderCancelled(cancelledEvent);
    }
  }

  /**
   * Mapper: Entity → DTO Response
   */
  private InventoryItemResponse toResponse(InventoryItem item) {
    return new InventoryItemResponse(
        item.getId(),
        item.getProductId(),
        item.getProductName(),
        item.getAvailableStock(),
        item.getReservedStock(),
        item.getTotalStock(),
        item.getCreatedAt(),
        item.getUpdatedAt()
    );
  }
}
