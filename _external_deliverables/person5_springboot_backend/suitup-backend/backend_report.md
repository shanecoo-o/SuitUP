# 📄 Relatório Técnico de Implementação — SuitUP Backend

Este relatório consolida a arquitetura, endpoints REST implementados, integridade do banco de dados e as soluções robustas formuladas para o ateliê industrial **SuitUP**.

---

## 🔹 1. APIs Implementadas

Abaixo está o resumo mapeado das rotas expostas pelo Spring Boot Tomcat Server na porta padrão `8080`:

| Método | Endpoint | Perfil Alvo | Descrição Técnica |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Qualquer | Registra novas contas de usuários cifrando credenciais via BCrypt hashes |
| **POST** | `/api/auth/login` | Qualquer | Confere credenciais e retorna o token JWT de portador unificado |
| **GET** | `/api/me` | Autenticado | Retorna detalhes reativos do usuário ativo atual |
| **POST** | `/api/orders` | `CLIENT` | Submete nova configuração de terno e medidas com código M-Pesa |
| **GET** | `/api/orders` | Qualquer | Listagem integral de todas as encomendas para auditoria |
| **GET** | `/api/orders/{id}` | Qualquer | Detalhes microscópicos de terno e passos do tracker industrial |
| **POST** | `/api/orders/{id}/payment-proof` | `CLIENT` | Upload via Multipart Form File de comprovantes físicos (limite 10MB) |
| **GET** | `/api/orders/{id}/payment-proof` | `ADMIN` / `CLIENT` | Retorna o caminho relativo da imagem e o status financeiro de triagem |
| **PUT** | `/api/orders/{id}/validate-payment` | `ADMIN` | Triagem contábil: Aprova ou Reprova depósitos M-Pesa |
| **PUT** | `/api/orders/{id}/status` | `TAILOR` / `ADMIN` | Modifica os estágios físicos do fato (Ex: `IN_PRODUCTION`, `DELIVERED`) |
| **POST** | `/api/orders/sync-batch` | `CLIENT` (Offline) | Recebe pacotes de compras de forma offline sem re-entradas duplicadas |

---

## 🔹 2. Estrutura e Dicionário de Dados SQL (PostgreSQL Schema)

Para assegurar consistência referencial, as entidades mapeiam de forma estrita duas coleções principais:

### 👤 Tabela: `suit_users`
Guarda perfis cadastrados no ateliê.
* `id` (bigint, PK, autoincrement): Identificador único.
* `name` (varchar(100)): Nome completo.
* `email` (varchar(100), Unique): Correio de login.
* `phone` (varchar(30)): Contacto celular.
* `password_hash` (varchar(200)): Senha blindada com BCrypt salt.
* `role` (varchar(30)): Tipo de permissões (`CLIENT`, `ADMIN`, `TAILOR`).
* `created_at` / `updated_at` (timestamp): Carimbos de auditoria local.

### 👔 Tabela: `suit_orders`
Guarda dados de customização de terno bespoke e medidas.
* `id` (varchar(50), PK): ID da Encomenda, gerado no backend (`SUIT-XXXX`) ou sincronizado.
* `client_name` / `client_email` / `client_phone`: Copiados para preservação histórica imutável das encomendas realizadas.
* `model` / `fabric` / `lapel` / `sleeves` / `buttons` / `pockets` / `lining` / `color_hex` / `fit_type` (varchar): Especificações estéticas premium.
* `shoulders` / `chest` / `waist` / `hips` / `sleeve_length` / `trouser_length` / `height` / `weight` (double precision): Métricas corporais em cm.
* `delivery_type` (varchar(30)): Tipo de envio (`DELIVERY` ou `PICKUP`).
* `delivery_address` / `pickup_point` (text): Informações de logística e despacho físico.
* `mpesa_transaction_id` (varchar(50), Unique): ID do recibo de pagamento M-Pesa.
* `payment_status` (varchar(30)): Status financeiro (`PENDING`, `PROOF_UPLOADED`, `VALIDATED`, `REJECTED`).
* `payment_proof_url` (varchar(255)): URL relativa de referência da captura multipart salva em disco.
* `order_status` (varchar(30)): Andamento físico do terno na fábrica.
* `total_price` (double precision): Valor total em meticais (MZN).
* `sync_pending` (boolean): Flag detector de encomendas off-line enviadas por lote.
* `client_generated_id` (varchar(100), Unique): UUID provisório offline gerado pela aplicação KMP para evitar reentradas idênticas.

---

## 🔹 3. Casos de Borda e Bugs Solucionados

* **Bug de Duplicidade M-Pesa**: Em encomendas criadas simultaneamente no Postman ou após oscilação de rede de dados móvel de Moçambique, duas faturas podiam tentar declarar o mesmo código de transação do M-Pesa. 
  * *Solução*: Adicionou-se uma restrição JPA dinâmica + `UNIQUE INDEX` na base. O método `createOrder` valida a existência do ID antes da persistência, disparando um erro limpo e impedindo fraude.
* **Resiliência do Lote de Sincronização**: Se dez pedidos capturados offline fossem enviados de uma vez e um deles possuísse ID repetido, a transação geral do lote falharia na abordagem SQL habitual.
  * *Solução*: Implementou-se um looping tolerante em `syncBatch`. Os elementos novos são sincronizados sob o prefixo único `SUIT-SYNC-`, e os duplicados são movidos para a propriedade array `ignoredIds` de forma fluida. O servidor responde indicando que o lote foi executado parcialmente com sucesso sem interromper as compras pendentes do cliente.
* **Malwares em Upload de Arquivos**: O upload multipart poderia sofrer ataques com injeção de sequências como `../` para gravação fora do diretório.
  * *Solução*: O `FileStorageService` purga o path original, utiliza hashes UUID randômicos para salvar os comprovativos, e recria caminhos sanitizados de forma segura.
