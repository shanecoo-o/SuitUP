INSERT INTO roles (id, code, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'CUSTOMER', 'Cliente'),
    ('00000000-0000-0000-0000-000000000002', 'ADMIN', 'Administrador')
ON CONFLICT (code) DO NOTHING;

DO $seed$
BEGIN
    IF ${seedDev} THEN
        INSERT INTO users (
            id,
            full_name,
            email,
            phone,
            password_hash,
            enabled
        ) VALUES (
            '00000000-0000-0000-0000-000000000101',
            'Administrador Local',
            'admin.local@suitup.test',
            '+258840000000',
            'DEV_ONLY_REPLACE_DURING_AUTH_PHASE',
            TRUE
        ) ON CONFLICT DO NOTHING;

        INSERT INTO user_roles (user_id, role_id) VALUES (
            '00000000-0000-0000-0000-000000000101',
            '00000000-0000-0000-0000-000000000002'
        ) ON CONFLICT DO NOTHING;

        INSERT INTO suit_models (
            id,
            name,
            category,
            description,
            price,
            fabric_type,
            color,
            image_key,
            active
        ) VALUES
            (
                '10000000-0000-0000-0000-000000000001',
                'Fato Clássico Preto',
                'Clássico',
                'Corte clássico em preto para eventos formais e uso profissional.',
                8500.00,
                'Lã Premium',
                'Preto',
                'suit_classic_black',
                TRUE
            ),
            (
                '10000000-0000-0000-0000-000000000002',
                'Fato Azul Executivo',
                'Executivo',
                'Fato azul-marinho para reuniões e cerimónias.',
                9500.00,
                'Lã Premium',
                'Azul Marinho',
                'suit_navy_business',
                TRUE
            ),
            (
                '10000000-0000-0000-0000-000000000003',
                'Fato Cinza Slim Fit',
                'Slim Fit',
                'Silhueta slim em cinza com visual moderno.',
                7800.00,
                'Algodão',
                'Cinza Grafite',
                'suit_grey_slim',
                TRUE
            ),
            (
                '10000000-0000-0000-0000-000000000004',
                'Fato Casual de Linho',
                'Casual',
                'Fato leve de linho para clima quente.',
                7200.00,
                'Linho',
                'Bege',
                'suit_casual_linen',
                TRUE
            ),
            (
                '10000000-0000-0000-0000-000000000005',
                'Fato Castanho Premium',
                'Premium',
                'Fato premium em tom castanho com acabamento distinto.',
                11000.00,
                'Cashmere',
                'Castanho',
                'suit_classic_black',
                TRUE
            ),
            (
                '10000000-0000-0000-0000-000000000006',
                'Smoking Preto',
                'Gala',
                'Smoking preto para gala e eventos nocturnos.',
                12500.00,
                'Lã Premium',
                'Preto',
                'suit_classic_black',
                TRUE
            )
        ON CONFLICT (id) DO NOTHING;
    END IF;
END
$seed$;
