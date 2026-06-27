-- ============================================================================
-- ARQUIVO: local_sqlite_schema.sql
-- ESQUEMA RELACIONAL LOCAL (OFFLINE STORAGE) - SQLITE
-- ============================================================================

DROP TABLE IF EXISTS sync_queue;
DROP TABLE IF EXISTS local_delivery_info;
DROP TABLE IF EXISTS local_payment_proofs;
DROP TABLE IF EXISTS local_order_status_history;
DROP TABLE IF EXISTS local_orders;
DROP TABLE IF EXISTS local_measurements;
DROP TABLE IF EXISTS local_suit_designs;

CREATE TABLE local_suit_designs (
    id TEXT PRIMARY KEY NOT NULL,
    user_id TEXT NOT NULL,
    suit_model_id TEXT,
    lapel_type TEXT,
    sleeve_style TEXT,
    button_style TEXT,
    pocket_style TEXT,
    lining_style TEXT,
    fabric_name TEXT,
    fabric_color_hex TEXT,
    fit_type TEXT,
    estimated_price REAL
);

CREATE TABLE local_measurements (
    id TEXT PRIMARY KEY NOT NULL,
    user_id TEXT NOT NULL,
    label TEXT NOT NULL,
    height REAL,
    weight REAL,
    shoulders REAL,
    chest REAL,
    waist REAL,
    hips REAL,
    neck REAL,
    arm_length REAL,
    sleeve_length REAL,
    trouser_length REAL,
    inseam REAL,
    notes TEXT
);

CREATE TABLE local_orders (
    id TEXT PRIMARY KEY NOT NULL,
    user_id TEXT,
    suit_design_id TEXT,
    measurement_id TEXT,
    order_code TEXT UNIQUE NOT NULL,
    total_price REAL NOT NULL,
    payment_status TEXT NOT NULL CHECK (payment_status IN ('PENDING', 'PROOF_UPLOADED', 'VALIDATED', 'REJECTED')),
    order_status TEXT NOT NULL CHECK (order_status IN ('PAYMENT_PENDING', 'PAYMENT_VALIDATED', 'IN_PRODUCTION', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED')),
    delivery_type TEXT CHECK (delivery_type IN ('DELIVERY', 'PICKUP')),
    created_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'NOW')),
    updated_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'NOW'))
);

CREATE TABLE local_order_status_history (
    id TEXT PRIMARY KEY NOT NULL,
    order_id TEXT NOT NULL,
    status TEXT NOT NULL,
    description TEXT,
    changed_by TEXT,
    created_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'NOW'))
);

CREATE TABLE local_payment_proofs (
    id TEXT PRIMARY KEY NOT NULL,
    order_id TEXT NOT NULL,
    mpesa_transaction_id TEXT UNIQUE NOT NULL,
    file_name TEXT NOT NULL,
    file_path TEXT NOT NULL,
    file_type TEXT,
    uploaded_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'NOW'))
);

CREATE TABLE local_delivery_info (
    id TEXT PRIMARY KEY NOT NULL,
    order_id TEXT UNIQUE NOT NULL,
    address TEXT,
    city TEXT,
    reference_point TEXT,
    pickup_point TEXT,
    receiver_name TEXT,
    receiver_phone TEXT,
    delivery_notes TEXT
);

CREATE TABLE sync_queue (
    id TEXT PRIMARY KEY NOT NULL,
    entity_type TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    operation_type TEXT NOT NULL CHECK (operation_type IN ('CREATE', 'UPDATE', 'DELETE', 'UPLOAD')),
    payload_json TEXT NOT NULL, -- JSON estruturado como string no SQLite
    status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SYNCED', 'FAILED')),
    retry_count INTEGER DEFAULT 0,
    last_error TEXT,
    created_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'NOW'))
);

-- Gatilhos nativos SQLite para emulação do comportamento 'updated_at'
CREATE TRIGGER trg_local_orders_update AFTER UPDATE ON local_orders
BEGIN
    UPDATE local_orders SET updated_at = STRFTIME('%Y-%m-%d %H:%M:%S', 'NOW') WHERE id = OLD.id;
END;
