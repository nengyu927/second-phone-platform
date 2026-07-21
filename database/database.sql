CREATE DATABASE IF NOT EXISTS second_phone_platform
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE second_phone_platform;

CREATE TABLE IF NOT EXISTS members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    account VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    storage_capacity VARCHAR(30),
    color VARCHAR(50),
    condition_level VARCHAR(30) NOT NULL DEFAULT 'GOOD',
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    image_url VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(12,2) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    shipping_address VARCHAR(300) NOT NULL,
    payment_method VARCHAR(30) NOT NULL DEFAULT 'CASH_ON_DELIVERY',
    order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    note TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_orders_member_id (member_id),
    INDEX idx_orders_product_id (product_id),
    INDEX idx_orders_status (order_status)
);

CREATE TABLE IF NOT EXISTS repair_orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    device_brand VARCHAR(50) NOT NULL,
    device_model VARCHAR(100) NOT NULL,
    imei VARCHAR(30),
    problem_description TEXT NOT NULL,
    repair_status VARCHAR(30) NOT NULL DEFAULT 'RECEIVED',
    estimated_cost DECIMAL(12,2),
    final_cost DECIMAL(12,2),
    received_date DATE NOT NULL,
    expected_completion_date DATE,
    completed_date DATE,
    technician_name VARCHAR(100),
    repair_notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_repairs_member_id (member_id),
    INDEX idx_repairs_status (repair_status)
);
