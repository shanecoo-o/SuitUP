-- ============================================================================
-- ARQUIVO: schema_postgresql.sql
-- ESQUEMA RELACIONAL CENTRAL (BACKEND) - POSTGRESQL 13+
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DROP TABLE IF EXISTS sync_queue CASCADE;
DROP TABLE IF EXISTS delivery_info CASCADE;
DROP TABLE IF EXISTS payment_proofs CASCADE;
DROP TABLE IF EXISTS order_status_history CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS measurements CASCADE;
DROP TABLE IF EXISTS suit_designs CASCADE;
DROP TABLE IF EXISTS suit_models CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,
    "role" VARCHAR(50) CHECK ("role" IN ('CLIENT', 'ADMIN', 'TAILOR')) DEFAULT 'CLIENT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suit_models (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suit_designs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    suit_model_id UUID REFERENCES suit_models(id) ON DELETE SET NULL,
    lapel_type VARCHAR(100),
    sleeve_style VARCHAR(100),
    button_style VARCHAR(100),
    pocket_style VARCHAR(100),
    lining_style VARCHAR(100),
    fabric_name VARCHAR(100),
    fabric_color_hex VARCHAR(10),
    fit_type VARCHAR(100),
    estimated_price DECIMAL(10,2),
    preview_image_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE measurements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    label VARCHAR(100) NOT NULL,
    height DECIMAL(6,2), 
    weight DECIMAL(6,2),
    shoulders DECIMAL(6,2),
    chest DECIMAL(6,2),
    waist DECIMAL(6,2),
    hips DECIMAL(6,2),
    neck DECIMAL(6,2),
    arm_length DECIMAL(6,2),
    sleeve_length DECIMAL(6,2),
    trouser_length DECIMAL(6,2),
    inseam DECIMAL(6,2),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    suit_design_id UUID REFERENCES suit_designs(id) ON DELETE SET NULL,
    measurement_id UUID REFERENCES measurements(id) ON DELETE RESTRICT, 
    order_code VARCHAR(50) UNIQUE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(50) CHECK (payment_status IN ('PENDING', 'PROOF_UPLOADED', 'VALIDATED', 'REJECTED')) DEFAULT 'PENDING',
    order_status VARCHAR(50) CHECK (order_status IN ('PAYMENT_PENDING', 'PAYMENT_VALIDATED', 'IN_PRODUCTION', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED')) DEFAULT 'PAYMENT_PENDING',
    delivery_type VARCHAR(50) CHECK (delivery_type IN ('DELIVERY', 'PICKUP')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    "status" VARCHAR(50) NOT NULL, 
    description TEXT,
    changed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_proofs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    mpesa_transaction_id VARCHAR(100) UNIQUE NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    validated BOOLEAN DEFAULT FALSE,
    validated_at TIMESTAMP WITH TIME ZONE,
    validation_notes TEXT
);

CREATE TABLE delivery_info (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    address VARCHAR(255),
    city VARCHAR(100),
    reference_point VARCHAR(255),
    pickup_point VARCHAR(255),
    receiver_name VARCHAR(255),
    receiver_phone VARCHAR(50),
    delivery_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sync_queue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    operation_type VARCHAR(50) CHECK (operation_type IN ('CREATE', 'UPDATE', 'DELETE', 'UPLOAD')),
    payload_json JSONB NOT NULL,
    "status" VARCHAR(50) CHECK ("status" IN ('PENDING', 'SYNCED', 'FAILED')) DEFAULT 'PENDING', 
    retry_count INT DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    synced_at TIMESTAMP WITH TIME ZONE
);

-- Triggers Procedimentais para Auditoria Temporal Automática
CREATE OR REPLACE FUNCTION trigger_refresh_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_timestamp BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION trigger_refresh_updated_at();
CREATE TRIGGER trg_suit_models_timestamp BEFORE UPDATE ON suit_models FOR EACH ROW EXECUTE FUNCTION trigger_refresh_updated_at();
CREATE TRIGGER trg_suit_designs_timestamp BEFORE UPDATE ON suit_designs FOR EACH ROW EXECUTE FUNCTION trigger_refresh_updated_at();
CREATE TRIGGER trg_measurements_timestamp BEFORE UPDATE ON measurements FOR EACH ROW EXECUTE FUNCTION trigger_refresh_updated_at();
CREATE TRIGGER trg_orders_timestamp BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION trigger_refresh_updated_at();
CREATE TRIGGER trg_delivery_info_timestamp BEFORE UPDATE ON delivery_info FOR EACH ROW EXECUTE FUNCTION trigger_refresh_updated_at();

CREATE INDEX idx_orders_user_lookup ON orders(user_id);
CREATE INDEX idx_orders_code_search ON orders(order_code);
CREATE INDEX idx_sync_queue_pending ON sync_queue("status") WHERE "status" = 'PENDING';
