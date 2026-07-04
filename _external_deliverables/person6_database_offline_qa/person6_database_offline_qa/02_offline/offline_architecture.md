# Arquitetura Offline-First - SuitUP

## 1. Visão Geral
O SuitUP foi desenhado com uma estratégia **Offline-first**, garantindo que a aplicação mobile atua como um **Fat Client**. Isto significa que a app contém lógica de negócio suficiente para permitir que o cliente navegue no catálogo, personalize o seu fato e crie pedidos mesmo em **operação parcialmente conectada** ou totalmente sem internet.

A espinha dorsal desta resiliência baseia-se num padrão de **Store-and-Forward**: os dados são primeiro guardados localmente e, posteriormente, reencaminhados para o servidor assim que a conectividade for restaurada.

## 2. Fluxos de Utilização Offline
O ciclo de vida de uma encomenda sem internet segue os seguintes passos:

1. **Catálogo em Local Cache:** O utilizador abre a app e visualiza os modelos de fatos guardados no cache local da última sincronização.
2. **Personalização Offline:** O utilizador acede ao Editor 2D, configura o fato e a app guarda o design (SQLite) localmente.
3. **Checkout Transparente:** O utilizador finaliza a encomenda.
4. **Fila de Sincronização (Sync Queue):** O pedido é guardado no banco local com a flag `syncPending = true` e é adicionado à `sync_queue`.
5. **Restauração de Conectividade:** Quando a internet volta, o *background worker* da app deteta a rede e envia o *payload* da fila para o backend.
6. **Validação do Servidor:** O backend (Spring Boot) processa o pedido e devolve uma confirmação de sucesso.
7. **Limpeza Local:** A app marca o pedido como sincronizado (`syncPending = false`) e remove a tarefa da fila.
8. **Atualização da UI:** O pedido passa a estar visível no ecrã de Tracking oficial com o status atribuído pelo servidor.

## 3. Conceitos Core Adotados
* **Client-generated ID:** Para permitir a criação offline, todos os IDs (`orders`, `suit_designs`) são UUIDs gerados pela própria app (cliente) e não pelo banco de dados do servidor. Isto garante que a app tem um ID válido imediatamente.
* **Idempotency:** O backend está preparado para receber o mesmo `Client-generated ID` várias vezes sem criar duplicados, garantindo segurança na sincronização.
* **Retry Policy:** Se a rede falhar durante o envio, a `sync_queue` aumenta o `retry_count` e tenta novamente seguindo um modelo de *backoff* exponencial.
* **Conflict Resolution:** O servidor atua sempre como a Fonte da Verdade. Conflitos de tracking ou pagamentos são resolvidos rejeitando o upload de provas duplicadas e mantendo as atualizações do Admin como prioritárias.
