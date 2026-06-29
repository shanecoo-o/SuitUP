ALTER TABLE roles
    ADD CONSTRAINT chk_roles_code
    CHECK (code IN ('CUSTOMER', 'ADMIN'));

ALTER TABLE uploaded_files
    ADD CONSTRAINT chk_uploaded_files_purpose
    CHECK (purpose IN ('SUIT_IMAGE', 'PAYMENT_PROOF', 'PROFILE', 'OTHER')),
    ADD CONSTRAINT chk_uploaded_files_size
    CHECK (size_bytes >= 0);

ALTER TABLE suit_models
    ADD CONSTRAINT chk_suit_models_price
    CHECK (price >= 0),
    ADD CONSTRAINT chk_suit_models_currency
    CHECK (currency = 'MZN');

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_status
    CHECK (status IN (
        'RECEIVED',
        'IN_ANALYSIS',
        'MEASUREMENTS_CONFIRMED',
        'IN_PRODUCTION',
        'READY_FOR_DELIVERY',
        'DELIVERED',
        'CANCELLED'
    )),
    ADD CONSTRAINT chk_orders_payment_status
    CHECK (payment_status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    ADD CONSTRAINT chk_orders_fulfillment_type
    CHECK (fulfillment_type IN ('DELIVERY', 'PICKUP')),
    ADD CONSTRAINT chk_orders_fulfillment_details
    CHECK (
        (
            fulfillment_type = 'DELIVERY'
            AND delivery_address IS NOT NULL
            AND BTRIM(delivery_address) <> ''
            AND pickup_location IS NULL
        )
        OR
        (
            fulfillment_type = 'PICKUP'
            AND pickup_location IS NOT NULL
            AND BTRIM(pickup_location) <> ''
            AND delivery_address IS NULL
        )
    ),
    ADD CONSTRAINT chk_orders_amounts
    CHECK (
        subtotal_amount >= 0
        AND delivery_fee >= 0
        AND total_amount = subtotal_amount + delivery_fee
    ),
    ADD CONSTRAINT chk_orders_currency
    CHECK (currency = 'MZN');

ALTER TABLE order_items
    ADD CONSTRAINT chk_order_items_quantity
    CHECK (quantity > 0),
    ADD CONSTRAINT chk_order_items_amounts
    CHECK (unit_price >= 0 AND line_total = unit_price * quantity);

ALTER TABLE measurements
    ADD CONSTRAINT chk_measurements_positive
    CHECK (
        height_cm > 0
        AND chest_cm > 0
        AND waist_cm > 0
        AND shoulders_cm > 0
        AND sleeve_cm > 0
        AND trouser_length_cm > 0
        AND (neck_cm IS NULL OR neck_cm > 0)
        AND (hip_cm IS NULL OR hip_cm > 0)
    );

ALTER TABLE payments
    ADD CONSTRAINT chk_payments_method
    CHECK (method IN ('MPESA', 'EMOLA', 'BANK_TRANSFER', 'CASH_ON_PICKUP')),
    ADD CONSTRAINT chk_payments_status
    CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    ADD CONSTRAINT chk_payments_amount
    CHECK (amount >= 0),
    ADD CONSTRAINT chk_payments_currency
    CHECK (currency = 'MZN'),
    ADD CONSTRAINT chk_payments_lifecycle
    CHECK (
        (status = 'PENDING' AND confirmed_at IS NULL AND rejected_at IS NULL)
        OR
        (status = 'CONFIRMED' AND confirmed_at IS NOT NULL AND rejected_at IS NULL)
        OR
        (status = 'REJECTED' AND rejected_at IS NOT NULL AND confirmed_at IS NULL)
    );

ALTER TABLE order_status_history
    ADD CONSTRAINT chk_order_history_old_status
    CHECK (old_status IS NULL OR old_status IN (
        'RECEIVED', 'IN_ANALYSIS', 'MEASUREMENTS_CONFIRMED', 'IN_PRODUCTION',
        'READY_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'
    )),
    ADD CONSTRAINT chk_order_history_new_status
    CHECK (new_status IN (
        'RECEIVED', 'IN_ANALYSIS', 'MEASUREMENTS_CONFIRMED', 'IN_PRODUCTION',
        'READY_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'
    ));

ALTER TABLE payment_status_history
    ADD CONSTRAINT chk_payment_history_old_status
    CHECK (old_status IS NULL OR old_status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    ADD CONSTRAINT chk_payment_history_new_status
    CHECK (new_status IN ('PENDING', 'CONFIRMED', 'REJECTED'));

ALTER TABLE idempotency_keys
    ADD CONSTRAINT chk_idempotency_expiration
    CHECK (expires_at > created_at);

CREATE UNIQUE INDEX ux_users_email_lower ON users (LOWER(email));
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);
CREATE INDEX idx_uploaded_files_owner ON uploaded_files (owner_user_id);
CREATE INDEX idx_uploaded_files_purpose_created ON uploaded_files (purpose, created_at DESC);
CREATE INDEX idx_suit_models_active_category ON suit_models (active, category, created_at DESC);
CREATE INDEX idx_orders_customer_created ON orders (customer_user_id, created_at DESC);
CREATE INDEX idx_orders_status_created ON orders (status, created_at DESC);
CREATE INDEX idx_orders_payment_status_created ON orders (payment_status, created_at DESC);
CREATE INDEX idx_order_items_order ON order_items (order_id);
CREATE INDEX idx_order_items_suit_model ON order_items (suit_model_id);
CREATE INDEX idx_payments_order_created ON payments (order_id, created_at DESC);
CREATE INDEX idx_payments_status_submitted ON payments (status, submitted_at DESC);
CREATE UNIQUE INDEX ux_payments_transaction_reference
    ON payments (method, transaction_reference)
    WHERE transaction_reference IS NOT NULL;
CREATE INDEX idx_order_status_history_order_created
    ON order_status_history (order_id, created_at ASC);
CREATE INDEX idx_payment_status_history_payment_created
    ON payment_status_history (payment_id, created_at ASC);
CREATE INDEX idx_idempotency_keys_expiration ON idempotency_keys (expires_at);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_suit_models_updated_at
    BEFORE UPDATE ON suit_models
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
