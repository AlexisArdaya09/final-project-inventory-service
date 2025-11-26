-- Script SQL para crear la base de datos y usuario para Inventory Service
-- Ejecutar como usuario postgres o superusuario

-- Crear base de datos
CREATE DATABASE ecommerce_inventory;

-- Crear usuario (opcional, si no usas el usuario postgres por defecto)
CREATE USER ecommerce_user WITH PASSWORD 'ecommerce_password';

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE ecommerce_inventory TO ecommerce_user;

-- Conectarse a la base de datos ecommerce_inventory
\c ecommerce_inventory

-- Otorgar privilegios en el esquema público
GRANT ALL ON SCHEMA public TO ecommerce_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ecommerce_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ecommerce_user;

-- Nota: Las tablas se crean automáticamente mediante JPA/Hibernate
-- con la configuración ddl-auto=create-drop o ddl-auto=update

