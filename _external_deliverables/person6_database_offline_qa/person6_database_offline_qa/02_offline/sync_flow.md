# Fluxo de Sincronização (Sync Flow) - SuitUP

## 1. Arquitetura de Comunicação (Topologia)
O fluxo de dados desde a interação do utilizador até à persistência no servidor segue este caminho estrito:

[ Mobile KMP (UI & Lógica) ] 
       ↓ 
[ Local SQLite / SQLDelight (Armazenamento Local) ]
       ↓
[ Sync Queue (Gestão de Tarefas Offline) ]
       ↓
[ Ktor Client (Camada de Rede KMP) ]
       ↓
[ Spring Boot API (Backend Server) ]
       ↓
[ PostgreSQL (Fonte de Verdade) ]

---

## 2. Casos de Uso de Sincronização

### Caso 1: Pedido criado online
* **Fluxo:** O utilizador cria o pedido. A app guarda localmente no SQLDelight e, detetando rede ativa, a `Sync Queue` dispara o Ktor Client imediatamente. O Spring Boot recebe, guarda no PostgreSQL e devolve o status `201 Created`. A app atualiza a UI para refletir o pedido confirmado.

### Caso 2: Pedido criado offline
* **Fluxo:** O utilizador cria o pedido num local sem rede. O pedido é salvo no SQLite e o evento (CREATE_ORDER) entra na `Sync Queue` com status `PENDING`. O utilizador vê a mensagem "Pedido guardado, aguardando rede". A app continua totalmente funcional.

### Caso 3: Upload de comprovativo falha
* **Fluxo:** A meio do envio de uma imagem de M-Pesa para o servidor, a rede cai (timeout). O Ktor Client lança uma exceção. A `Sync Queue` apanha o erro, incrementa o `retry_count`, guarda a mensagem de erro no campo `last_error` e altera o status da tarefa temporariamente para `FAILED` (com re-tentativa agendada).

### Caso 4: Pedido duplicado (Idempotência)
* **Fluxo:** Devido a instabilidade na rede, a app envia o pedido, o servidor guarda no PostgreSQL, mas a resposta de sucesso perde-se antes de chegar ao telemóvel. A app acha que falhou e tenta enviar de novo mais tarde. O Spring Boot lê o `UUID` (Client-generated ID), verifica que o pedido já existe, ignora a re-criação e devolve apenas um `200 OK` amigável para avisar a app para marcar como sincronizado.

### Caso 5: A Internet volta
* **Fluxo:** O sistema operativo avisa a app que há rede. O *worker* acorda, lê a `Sync Queue` (ordenada por `created_at` ASC), processa a fila sequencialmente garantindo que `suit_designs` sejam enviados antes de `orders` (para não quebrar a Foreign Key no servidor) e atualiza o SQLite com `syncPending = 0`.

### Caso 6: Backend rejeita o pagamento (M-Pesa ID já usado)
* **Fluxo:** A app sincroniza o pedido perfeitamente, mas o `mpesa_transaction_id` já tinha sido usado por outro utilizador (fraude ou erro humano). O PostgreSQL bloqueia devido à constraint `UNIQUE`. O Spring Boot devolve um `400 Bad Request`. A app marca o status do pedido localmente como `PAYMENT_REJECTED` e alerta o cliente com uma notificação push/local para enviar um novo comprovativo.
