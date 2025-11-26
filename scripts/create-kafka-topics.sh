#!/bin/bash

# Script para crear los topics de Kafka necesarios para el sistema de e-commerce
# Este script debe ejecutarse dentro del contenedor de Kafka o con acceso a Kafka

KAFKA_BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVER:-localhost:9092}

echo "Creando topics de Kafka en $KAFKA_BOOTSTRAP_SERVER..."

# Topic para eventos de productos creados
kafka-topics --create \
  --bootstrap-server $KAFKA_BOOTSTRAP_SERVER \
  --topic ecommerce.products.created \
  --partitions 5 \
  --replication-factor 1 \
  --if-not-exists

# Topic para órdenes colocadas (usado por order-service, consumido por inventory-service)
kafka-topics --create \
  --bootstrap-server $KAFKA_BOOTSTRAP_SERVER \
  --topic ecommerce.orders.placed \
  --partitions 5 \
  --replication-factor 1 \
  --if-not-exists

# Topic para órdenes confirmadas (publicado por inventory-service)
kafka-topics --create \
  --bootstrap-server $KAFKA_BOOTSTRAP_SERVER \
  --topic ecommerce.orders.confirmed \
  --partitions 5 \
  --replication-factor 1 \
  --if-not-exists

# Topic para órdenes canceladas (publicado por inventory-service)
kafka-topics --create \
  --bootstrap-server $KAFKA_BOOTSTRAP_SERVER \
  --topic ecommerce.orders.cancelled \
  --partitions 5 \
  --replication-factor 1 \
  --if-not-exists

echo "Topics creados exitosamente!"
echo ""
echo "Listando topics existentes:"
kafka-topics --list --bootstrap-server $KAFKA_BOOTSTRAP_SERVER

