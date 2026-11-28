-- ====================================================================
-- SUPABASE / POSTGRESQL SCHEMA FOR STASH (oop-core-finals)
-- Run this entire script in Supabase's SQL Editor (SQL Editor -> New Query -> Run)
-- ====================================================================

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. PRODUCTS TABLE
CREATE TABLE IF NOT EXISTS products (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(100) UNIQUE NOT NULL,
    price_centavos INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 5,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. BUNDLES TABLE
CREATE TABLE IF NOT EXISTS bundles (
    bundle_id SERIAL PRIMARY KEY,
    bundle_name VARCHAR(100) UNIQUE NOT NULL,
    price_centavos BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. BUNDLE ITEMS TABLE
CREATE TABLE IF NOT EXISTS bundle_items (
    bundle_id INT NOT NULL REFERENCES bundles(bundle_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(product_id),
    quantity INT NOT NULL,
    PRIMARY KEY (bundle_id, product_id)
);

-- 5. SALES TABLE
CREATE TABLE IF NOT EXISTS sales (
    sale_id BIGSERIAL PRIMARY KEY,
    processed_by INT NOT NULL REFERENCES users(user_id),
    total_centavos BIGINT NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. SALE ITEMS TABLE
CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL REFERENCES sales(sale_id) ON DELETE CASCADE,
    product_id INT NULL REFERENCES products(product_id),
    bundle_id INT NULL REFERENCES bundles(bundle_id),
    item_name_snapshot VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price_centavos_snapshot BIGINT NOT NULL,
    subtotal_centavos BIGINT NOT NULL
);

-- 7. SALE BUNDLE COMPONENTS TABLE
CREATE TABLE IF NOT EXISTS sale_bundle_components (
    component_id BIGSERIAL PRIMARY KEY,
    sale_item_id BIGINT NOT NULL REFERENCES sale_items(sale_item_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(product_id),
    component_name_snapshot VARCHAR(100) NOT NULL,
    quantity_per_bundle INT NOT NULL,
    total_deducted_quantity INT NOT NULL
);

-- 8. STOCK MOVEMENTS TABLE (Audit Trail)
CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(product_id),
    change_amount INT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    reference_id BIGINT NULL,
    created_by INT NOT NULL REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. STOCK CHANGE REQUESTS TABLE
CREATE TABLE IF NOT EXISTS stock_change_requests (
    request_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(product_id),
    requested_quantity INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_by INT NOT NULL REFERENCES users(user_id),
    reviewed_by INT NULL REFERENCES users(user_id),
    review_notes VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL
);
