ALTER TABLE suit_models
    ALTER COLUMN currency TYPE VARCHAR(3)
    USING BTRIM(currency)::VARCHAR(3);

ALTER TABLE orders
    ALTER COLUMN currency TYPE VARCHAR(3)
    USING BTRIM(currency)::VARCHAR(3);

ALTER TABLE payments
    ALTER COLUMN currency TYPE VARCHAR(3)
    USING BTRIM(currency)::VARCHAR(3);
