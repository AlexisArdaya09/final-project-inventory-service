package com.alexisardaya.inventoryservice.repository;

import com.alexisardaya.inventoryservice.model.InventoryItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

  /**
   * Buscar item de inventario por productId (FK lógico)
   */
  Optional<InventoryItem> findByProductId(Long productId);

  /**
   * Verificar si existe item para un producto específico
   */
  boolean existsByProductId(Long productId);
}
