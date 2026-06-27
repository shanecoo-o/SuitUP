# Dicionário de Dados - Project SuitUP (Atualizado)
Este documento detalha o modelo relacional, restrições e regras de integridade aplicadas na base de dados central (PostgreSQL) do **Project SuitUP**, servindo de referência oficial para as equipas de Backend, Mobile e Controlo de Qualidade (QA).
## 1. Tabela: users
**Objetivo:** Registar todos os utilizadores do ecossistema (Clientes, Alfaiates e Administradores).
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador universal único do utilizador. |
| full_name | VARCHAR(255) | Sim | - | Nome completo do utilizador. |
| email | VARCHAR(255) | Sim | UNIQUE | Endereço de correio eletrónico credenciado. |
| phone | VARCHAR(50) | Não | - | Contacto telefónico principal. |
| password_hash | VARCHAR(255) | Sim | - | Senha encriptada de acesso. |
| "role" | VARCHAR(50) | Sim | CHECK ('CLIENT', 'ADMIN', 'TAILOR') | Nível de privilégio. Padrão: 'CLIENT'. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Registo cronológico de criação do perfil. |
| updated_at | TIMESTAMP TZ | Sim | Automatizado via Trigger | Data/hora da última modificação estrutural. |
 * **Automação (ECA):** Possui o gatilho trg_users_timestamp que executa a função procedural refresh_updated_at_column() antes de qualquer instrução UPDATE.
## 2. Tabela: suit_models
**Objetivo:** Catálogo descritivo dos modelos de fatos base disponibilizados pela alfaiataria.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador único do modelo de fato. |
| name | VARCHAR(255) | Sim | - | Nome do modelo (ex: 'Fato Italiano'). |
| description | TEXT | Não | - | Detalhes comerciais e especificações estilísticas. |
| base_price | DECIMAL(10,2) | Sim | - | Preço base de corte antes de customizações. |
| image_url | VARCHAR(500) | Não | - | Caminho para a imagem de catálogo. |
| active | BOOLEAN | Sim | Default: TRUE | Define a visibilidade do modelo no editor da app. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Data de inclusão do modelo no sistema. |
| updated_at | TIMESTAMP TZ | Sim | Automatizado via Trigger | Data de modificação do catálogo. |
 * **Automação (ECA):** Controlado pelo gatilho trg_suit_models_timestamp para sincronização temporal em instruções UPDATE.
## 3. Tabela: suit_designs
**Objetivo:** Armazenar as customizações exclusivas (lapelas, botões, tecidos) efetuadas pelos clientes no Editor 2D.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador único do design customizado. |
| user_id | UUID | Sim | FOREIGN KEY -> users(id) | Dono do design. **ON DELETE CASCADE**. |
| suit_model_id | UUID | Não | FOREIGN KEY -> suit_models(id) | Modelo base. **ON DELETE SET NULL**. |
| lapel_type | VARCHAR(100) | Não | - | Estilo da lapela selecionada. |
| sleeve_style | VARCHAR(100) | Não | - | Tipo de punho ou manga. |
| button_style | VARCHAR(100) | Não | - | Configuração e tipo de botões. |
| pocket_style | VARCHAR(100) | Não | - | Estilo dos bolsos. |
| lining_style | VARCHAR(100) | Não | - | Padrão do forro interior. |
| fabric_name | VARCHAR(100) | Não | - | Nome comercial do tecido selecionado. |
| fabric_color_hex | VARCHAR(10) | Não | - | Código hexadecimal da cor do tecido. |
| fit_type | VARCHAR(100) | Não | - | Tipo de ajuste (Slim Fit, Classic, etc.). |
| estimated_price | DECIMAL(10,2) | Não | - | Preço calculado após somar customizações. |
| preview_image_url | VARCHAR(500) | Não | - | Imagem renderizada do fato customizado. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Data de criação do design. |
| updated_at | TIMESTAMP TZ | Sim | Automatizado via Trigger | Última alteração no editor de design. |
 * **Automação (ECA):** Controlado pelo gatilho trg_suit_designs_timestamp.
## 4. Tabela: measurements
**Objetivo:** Centralizar as métricas corporais e dados físicos essenciais para a confecção à medida.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador único da ficha de medidas. |
| user_id | UUID | Sim | FOREIGN KEY -> users(id) | Cliente associado. **ON DELETE CASCADE**. |
| label | VARCHAR(100) | Sim | - | Nome da ficha (ex: 'Medidas Ajuste Verão'). |
| height | DECIMAL(6,2) | Não | - | Altura física em centímetros. |
| weight | DECIMAL(6,2) | Não | - | Peso corporal em quilogramas. |
| shoulders | DECIMAL(6,2) | Não | - | Medida dos ombros. |
| chest | DECIMAL(6,2) | Não | - | Perímetro do tórax. |
| waist | DECIMAL(6,2) | Não | - | Perímetro da cintura. |
| hips | DECIMAL(6,2) | Não | - | Perímetro da bacia/ancas. |
| neck | DECIMAL(6,2) | Não | - | Perímetro do pescoço. |
| arm_length | DECIMAL(6,2) | Não | - | Comprimento total do braço. |
| sleeve_length | DECIMAL(6,2) | Não | - | Comprimento da manga. |
| trouser_length | DECIMAL(6,2) | Não | - | Comprimento exterior das calças. |
| inseam | DECIMAL(6,2) | Não | - | Medida do entrepernas. |
| notes | TEXT | Não | - | Observações clínicas ou anatómicas do alfaiate. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Data da sessão de alfaiataria. |
| updated_at | TIMESTAMP TZ | Sim | Automatizado via Trigger | Data de retificação de medidas. |
 * **Automação (ECA):** Controlado pelo gatilho trg_measurements_timestamp.
## 5. Tabela: orders
**Objetivo:** Controlar as vendas, o progresso produtivo da confecção e o estado transacional financeiro.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador único do pedido corporativo. |
| user_id | UUID | Não | FOREIGN KEY -> users(id) | Cliente requerente. **ON DELETE SET NULL**. |
| suit_design_id | UUID | Não | FOREIGN KEY -> suit_designs(id) | Design do fato comprado. **ON DELETE SET NULL**. |
| measurement_id | UUID | Sim | FOREIGN KEY -> measurements(id) | Medidas vinculadas. **ON DELETE RESTRICT**. |
| order_code | VARCHAR(50) | Sim | UNIQUE | Código de rastreio legível (ex: 'ORD-001'). |
| total_price | DECIMAL(10,2) | Sim | - | Valor total faturado e cobrado ao cliente. |
| payment_status | VARCHAR(50) | Sim | CHECK ('PENDING', 'PROOF_UPLOADED', 'VALIDATED', 'REJECTED') | Estado financeiro. Padrão: 'PENDING'. |
| order_status | VARCHAR(50) | Sim | CHECK ('PAYMENT_PENDING', 'PAYMENT_VALIDATED', 'IN_PRODUCTION', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED') | Estado fabril/logístico. Padrão: 'PAYMENT_PENDING'. |
| delivery_type | VARCHAR(50) | Não | CHECK ('DELIVERY', 'PICKUP') | Modalidade de distribuição selecionada. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Data de submissão do carrinho de compras. |
| updated_at | TIMESTAMP TZ | Sim | Automatizado via Trigger | Mudança de estado ou tracking logístico. |
 * **Integridade Crítica:** A política **ON DELETE RESTRICT** no campo measurement_id impede que dados corporais históricos associados a uma fatura comercial ativa sejam acidentalmente eliminados por utilizadores.
 * **Automação (ECA):** Controlado pelo gatilho trg_orders_timestamp.
## 6. Tabela: order_status_history
**Objetivo:** Trilha de auditoria cronológica pura (Auditoria Física) das mudanças de estado operacionais.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador único da entrada de auditoria. |
| order_id | UUID | Sim | FOREIGN KEY -> orders(id) | Pedido associado. **ON DELETE CASCADE**. |
| "status" | VARCHAR(50) | Sim | - | Estado registado no momento da mudança. |
| description | TEXT | Não | - | Justificação da mudança ou notas operacionais. |
| changed_by | UUID | Não | FOREIGN KEY -> users(id) | Operador que realizou a ação. **ON DELETE SET NULL**. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Carimbo temporal imutável do evento. |
## 7. Tabela: payment_proofs
**Objetivo:** Rastrear as transações móveis de pagamento (M-Pesa) e guardar o caminho dos ficheiros físicos de validação financeira.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador único do comprovativo. |
| order_id | UUID | Sim | FOREIGN KEY -> orders(id) | Pedido financiado. **ON DELETE CASCADE**. |
| mpesa_transaction_id | VARCHAR(100) | Sim | UNIQUE | Código de ID único retornado pela API M-Pesa. |
| file_name | VARCHAR(255) | Sim | - | Nome original do ficheiro submetido. |
| file_path | VARCHAR(500) | Sim | - | Caminho absoluto de armazenamento (Cloud Storage/S3). |
| file_type | VARCHAR(50) | Não | - | MIME type do ficheiro (ex: 'application/pdf'). |
| uploaded_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Momento do upload pelo utilizador. |
| validated | BOOLEAN | Sim | Default: FALSE | Controlo interno se o admin aprovou o valor. |
| validated_at | TIMESTAMP TZ | Não | - | Data da auditoria financeira humana. |
| validation_notes | TEXT | Não | - | Motivos de rejeição ou observações bancárias. |
## 8. Tabela: delivery_info
**Objetivo:** Gestão estrita dos dados logísticos de expedição ao domicílio ou retenção em postos de recolha.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Identificador da rota logístico-operacional. |
| order_id | UUID | Sim | UNIQUE, FOREIGN KEY -> orders(id) | Relação estrita 1:1 com o pedido. **ON DELETE CASCADE**. |
| address | VARCHAR(255) | Não | - | Endereço detalhado de entrega física (Modo DELIVERY). |
| city | VARCHAR(100) | Não | - | Cidade destino de expedição. |
| reference_point | VARCHAR(255) | Não | - | Indicações geográficas para o estafeta. |
| pickup_point | VARCHAR(255) | Não | - | Nome/Código da loja física escolhida (Modo PICKUP). |
| receiver_name | VARCHAR(255) | Não | - | Nome da pessoa designada para aceitar a encomenda. |
| receiver_phone | VARCHAR(50) | Não | - | Contacto móvel do recetor da mercadoria. |
| delivery_notes | TEXT | Não | - | Instruções especiais da transportadora. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Data de planeamento de logística. |
| updated_at | TIMESTAMP TZ | Sim | Automatizado via Trigger | Data de reencaminhamento ou trânsito da carga. |
 * **Automação (ECA):** Controlado pelo gatilho trg_delivery_info_timestamp.
## 9. Tabela: sync_queue
**Objetivo:** Fila transacional centralizada para gerir a concorrência, replicação e tolerância a falhas na estratégia offline-first.
| Campo | Tipo | Obrigatório | Restrições / Chaves | Descrição |
|---|---|---|---|---|
| id | UUID | Sim | PRIMARY KEY (Default: gen_random_uuid()) | Código de controlo da mensagem na fila. |
| entity_type | VARCHAR(100) | Sim | - | Tabela destino afetada (ex: 'orders', 'measurements'). |
| entity_id | UUID | Sim | - | ID universal da entidade que sofreu a mutação. |
| operation_type | VARCHAR(50) | Sim | CHECK ('CREATE', 'UPDATE', 'DELETE', 'UPLOAD') | Operação DML de manipulação executada na app. |
| payload_json | JSONB | Sim | - | Estrutura completa dos dados modificados de forma binária. |
| "status" | VARCHAR(50) | Sim | CHECK ('PENDING', 'SYNCED', 'FAILED') | Estado da transação distribuída. Padrão: 'PENDING'. |
| retry_count | INT | Sim | Default: 0 | Contador de tentativas de escoamento em falhas de rede. |
| last_error | TEXT | Não | - | Stacktrace ou mensagem de erro devolvida pelo servidor. |
| created_at | TIMESTAMP TZ | Sim | Default: CURRENT_TIMESTAMP | Momento em que a operação entrou na fila local. |
| synced_at | TIMESTAMP TZ | Não | - | Momento exato em que ocorreu o COMMIT central. |
## 10. Índices de Performance Mapeados (Camada de Otimização)
Para evitar tabelas de varrimento completo (*Sequential Scans*) e otimizar as consultas SQL mais frequentes executadas pelo backend, os seguintes índices encontram-se ativos:
 * **idx_orders_user_lookup:** Otimiza a consulta à API do cliente para renderizar o ecrã "Os Meus Pedidos".
 * **idx_orders_code_search:** Garante respostas imediatas em pesquisas na barra de pesquisa administrativa por código de rastreio (order_code).
 * **idx_sync_queue_pending:** Índice parcial (*Partial Index*) focado em acelerar o varrimento de sincronizações que se encontram estritamente com o estado 'PENDING', ignorando os registos já arquivados.
 * **idx_payment_proofs_order:** Acelera as consultas cruzadas no painel de conciliação financeira de faturas.
