-- ============================================================
-- Pre-Orders table for STASH
-- Run this once in the Supabase SQL Editor.
-- ============================================================

CREATE TABLE IF NOT EXISTS pre_orders (
    order_id        SERIAL PRIMARY KEY,
    customer_name   VARCHAR(100) NOT NULL,
    contact_number  VARCHAR(20),
    -- exactly one of bundle_id / product_id must be non-null
    bundle_id       INT REFERENCES bundles(bundle_id)   ON DELETE SET NULL,
    product_id      INT REFERENCES products(product_id) ON DELETE SET NULL,
    quantity        INT NOT NULL CHECK (quantity > 0),
    total_centavos  BIGINT NOT NULL CHECK (total_centavos >= 0),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','READY','FULFILLED','CANCELLED')),
    notes           VARCHAR(500),
    created_by      INT NOT NULL REFERENCES users(user_id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- enforce: exactly one of bundle_id / product_id is set
    CONSTRAINT chk_preorder_item
        CHECK (
            (bundle_id IS NOT NULL AND product_id IS NULL)
            OR
            (product_id IS NOT NULL AND bundle_id IS NULL)
        )
);

-- Index for fast status-based filtering (staff dashboard)
CREATE INDEX IF NOT EXISTS idx_preorders_status
    ON pre_orders (status);

-- Index for looking up orders by customer name
CREATE INDEX IF NOT EXISTS idx_preorders_customer
    ON pre_orders (LOWER(customer_name));
