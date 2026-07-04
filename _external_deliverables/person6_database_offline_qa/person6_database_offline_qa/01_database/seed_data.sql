Inserção de Utilizadores (1 admin, 1 tailor/alfaiate, 1 cliente)
INSERT INTO users (id, role, name, email) VALUES
(1, 'admin', 'Administrador Principal', 'admin@sistema.pt'),
(2, 'tailor', 'Mestre Alfaiate', 'alfaiate@sistema.pt'),
(3, 'client', 'Cliente Teste', 'cliente@sistema.pt');

-- 2. Inserção de Modelos de Fatos
INSERT INTO suit_models (id, name, description, base_price) VALUES
(1, 'Fato Clássico', 'Modelo tradicional de 2 peças em lã', 250.00),
(2, 'Fato Slim Fit', 'Modelo moderno ajustado ao corpo', 280.00);

-- 3. Inserção de Medidas
INSERT INTO measurements (id, client_id, chest, waist, sleeve, length) VALUES
(1, 3, 105.5, 90.0, 65.0, 75.0);

-- 4. Inserção de Pedidos
INSERT INTO orders (id, client_id, tailor_id, suit_model_id, order_date) VALUES
(1, 3, 2, 1, '2026-06-17');

-- 5. Histórico de Estados dos Pedidos
INSERT INTO order_status_history (id, order_id, status_name, updated_at) VALUES
(1, 1, 'Pendente', '2026-06-17 10:00:00'),
(2, 1, 'Em Confecção', '2026-06-17 14:30:00');

-- 6. Inserção de Comprovativos Fictícios
INSERT INTO payment_proofs (id, order_id, file_path, upload_date) VALUES
(1, 1, '/docs/payments/comprovativo_001.pdf', '2026-06-17 10:05:00');

-- 7. Dados de Entrega
INSERT INTO deliveries (id, order_id, delivery_address, expected_date, delivery_status) VALUES
(1, 1, 'Avenida Marginal, Maputo', '2026-06-25', 'Agendado');
