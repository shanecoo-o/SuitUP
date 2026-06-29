DO $seed$
BEGIN
    IF ${seedDev} THEN
        UPDATE users
        SET email = 'admin@suitup.local',
            password_hash = '$2a$12$3jdaLQ9fKPYtQ44R8hvnuOzoMQCtdv0QHnIlMegGKPR346SIsBq/m',
            updated_at = CURRENT_TIMESTAMP
        WHERE id = '00000000-0000-0000-0000-000000000101';
    END IF;
END
$seed$;
