-- ====================================================================
-- RENTAL SERVICE SCHEMA FOR STASH (PostgreSQL / Supabase)
-- Run this in Supabase SQL Editor after schema_supabase.sql
-- ====================================================================

-- 1. RENTALS TABLE
CREATE TABLE IF NOT EXISTS rentals (
    rental_id SERIAL PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(30) NOT NULL DEFAULT '',
    venue VARCHAR(255) NOT NULL DEFAULT '',
    rental_start TIMESTAMP NOT NULL,
    expected_return TIMESTAMP NOT NULL,
    actual_return TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rentals_status ON rentals(status);
CREATE INDEX IF NOT EXISTS idx_rentals_customer ON rentals(customer_name);

-- 2. RENTAL ITEMS TABLE
CREATE TABLE IF NOT EXISTS rental_items (
    rental_item_id SERIAL PRIMARY KEY,
    rental_id INT NOT NULL REFERENCES rentals(rental_id) ON DELETE CASCADE,
    product_id INT NULL REFERENCES products(product_id),
    product_name VARCHAR(120) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    rental_price_centavos INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_rental_items_rental_id ON rental_items(rental_id);

-- 3. RENTAL PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS rental_payments (
    payment_id SERIAL PRIMARY KEY,
    rental_id INT NOT NULL REFERENCES rentals(rental_id) ON DELETE CASCADE,
    amount_centavos INT NOT NULL,
    payment_method VARCHAR(30) NOT NULL DEFAULT 'CASH',
    payment_date TIMESTAMP NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX IF NOT EXISTS idx_rental_payments_rental_id ON rental_payments(rental_id);
