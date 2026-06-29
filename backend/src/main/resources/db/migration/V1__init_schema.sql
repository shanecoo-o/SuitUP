CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(200) NOT NULL,
    email VARCHAR(320) NOT NULL,
    phone VARCHAR(32),
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE uploaded_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    purpose VARCHAR(32) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(150) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(1000) NOT NULL UNIQUE,
    public_url VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suit_models (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    price NUMERIC(14, 2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'MZN',
    fabric_type VARCHAR(100) NOT NULL,
    color VARCHAR(100) NOT NULL,
    image_key VARCHAR(150),
    primary_image_file_id UUID REFERENCES uploaded_files(id) ON DELETE SET NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    customer_name VARCHAR(200) NOT NULL,
    customer_phone VARCHAR(32) NOT NULL,
    customer_email VARCHAR(320),
    status VARCHAR(40) NOT NULL DEFAULT 'RECEIVED',
    payment_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    fulfillment_type VARCHAR(20) NOT NULL,
    delivery_address TEXT,
    pickup_location VARCHAR(300),
    notes TEXT,
    subtotal_amount NUMERIC(14, 2) NOT NULL,
    delivery_fee NUMERIC(14, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(14, 2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'MZN',
    idempotency_key VARCHAR(150) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    suit_model_id UUID REFERENCES suit_models(id) ON DELETE SET NULL,
    suit_name_snapshot VARCHAR(200) NOT NULL,
    category_snapshot VARCHAR(100) NOT NULL,
    fabric_snapshot VARCHAR(100) NOT NULL,
    color_snapshot VARCHAR(100) NOT NULL,
    design_snapshot JSONB NOT NULL,
    unit_price NUMERIC(14, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    line_total NUMERIC(14, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE measurements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    height_cm NUMERIC(7, 2) NOT NULL,
    chest_cm NUMERIC(7, 2) NOT NULL,
    waist_cm NUMERIC(7, 2) NOT NULL,
    shoulders_cm NUMERIC(7, 2) NOT NULL,
    sleeve_cm NUMERIC(7, 2) NOT NULL,
    trouser_length_cm NUMERIC(7, 2) NOT NULL,
    neck_cm NUMERIC(7, 2),
    hip_cm NUMERIC(7, 2),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    method VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    amount NUMERIC(14, 2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'MZN',
    transaction_reference VARCHAR(150),
    proof_file_id UUID REFERENCES uploaded_files(id) ON DELETE SET NULL,
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMPTZ,
    rejected_at TIMESTAMPTZ,
    reviewed_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    rejection_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    old_status VARCHAR(40),
    new_status VARCHAR(40) NOT NULL,
    changed_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    changed_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE idempotency_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(150) NOT NULL UNIQUE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    request_hash VARCHAR(128) NOT NULL,
    resource_type VARCHAR(80) NOT NULL,
    resource_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL
);
