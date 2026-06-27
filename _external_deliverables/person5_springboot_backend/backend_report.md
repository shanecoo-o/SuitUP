# Relatório Técnico do Backend: Spring Boot

Este relatório documenta as especificações de desenvolvimento, cobertura de rotas, estrutura da base de dados e estratégias de contingência técnica elaboradas para o ecossistema Alfaiataria Bespoke **SuitUP** (Universidade Zambeze — Moçambique).

---

## 🚀 APIs Implementadas

Todas as APIs construídas seguem as convenções RESTful de desenvolvimento, integradas com **Spring Security** para encriptação dinâmica e **JWT (JSON Web Tokens)** para manter sessões *stateless* e seguras.

| Classe / Controlador | Método HTTP | Endpoint | Permissão de Acesso | Descrição |
| :--- | :--- | :--- | :--- | :--- |
| **AuthController** | `POST` | `/api/auth/register` | Permit All (Público) | Cadastro de utilizadores (Clientes / Administradores) com senha encriptada em BCrypt. |
| **AuthController** | `POST` | `/api/auth/login` | Permit All (Público) | Autenticação de credenciais com retorno de Token JWT Bearer e claims de usuário. |
| **ModelController** | `GET` | `/api/models` | Permit All (Público) | Catálogo reativo de moldes mestres (Classic Slim-Fit, Modern Trespassado, Custom Imperial). |
| **SuitOrderController** | `POST` | `/api/orders` | `CLIENTE`, `ADMIN` | Submissão de novas escolhas de design e medidas físicas para corte. |
| **SuitOrderController** | `GET` | `/api/orders/{id}` | `CLIENTE`, `ADMIN` | Consulta e rastreio de progresso físico de confecção de um fato sob medida. |
| **SuitOrderController** | `POST` | `/api/orders/payment-proof` | `CLIENTE`, `ADMIN` | Multipart Upload da foto/screenshot de comprovativo de transferência do M-Pesa. |
| **SuitOrderController** | `POST` | `/api/orders/sync-batch` | `CLIENTE`, `ADMIN` | Sincronização em Lote reativa de pedidos criados offline (*Store-and-Forward*). |
| **SuitOrderController** | `PUT` | `/api/orders/{id}/validate-payment` | `ADMIN` (Apenas) | Validação administrativa do M-Pesa pelo alfaiate, avançando o estado de produção. |
| **SuitOrderController** | `PUT` | `/api/orders/{id}/status` | `ADMIN` (Apenas) | Transição manual do estado de produção das oficinas (Corte, Confecção Manual, Finalizado). |

---

## 🧪 Rotas Testadas

Os ensaios foram realizados simulando os fluxos industriais via ferramenta **Postman** e scripts automatizados de cobertura, garantindo respostas rápidas sob condições tropicais de rede limítrofe:

1. **Submissão e Validação M-Pesa**:
   * Testado o envio de transações duplicadas de Id M-Pesa. O sistema responde com o código HTTP `400 Bad Request` informando `"O ID de Transação M-Pesa indicado já foi resgatado para outra encomenda"`.
   * Envio de ficheiro de imagem real (.jpg e .png) em multipart no endpoint `/api/orders/payment-proof`. Armazenamento em diretório estático concluído com sucesso e link gerado no formato `/uploads/payment_proofs/uuid_timestamp.png`.
2. **Ciclo Completo de Autenticação JWT**:
   * Envio de credenciais válidas retorna objeto de usuário com token codificado em HMAC SHA-256 de 8 horas de expiração.
   * Tentativas de alteração de estados físicos de tracking via `/api/orders/{id}/status` feitas sem token válido ou pertencentes ao perfil cliente retornam erro de proteção de escopo `403 Forbidden` do Spring Security.
3. **Resiliência Offline (Sync Batch)**:
   * Sincronização de 5 encomendas criadas sem internet de forma sequencial tratada corretamente pelo controlador, retornando HTTP `200 OK` e persistindo as records sem colisão em base de dados central.

---

## ⛑️ Bugs e Resolução

Durante os ciclos de testes e escrita arquitetural com o Spring Boot 3.x, foram identificados e resolvidos os seguintes comportamentos anómalos:

1. **Bug de Caminho Relativo e File Traversal no Multipart Upload**:
   * *Sintoma*: Clientes submetendo nomes de arquivos contendo termos de diretório superior (ex: `../../comprovativo.png`) podiam expor pastas privadas do servidor.
   * *Solução*: Adicionado tratador regex `StringUtils.cleanPath(file.getOriginalFilename())` com bloqueio explícito de padrões de navegação e extensão para salvaguardar a integridade do contentor de produção.
2. **CORS Bloqueado ao Testar com Vite Local**:
   * *Sintoma*: O navegador rejeitava chamadas externas de XMLHttpRequest devido à proteção de origem.
   * *Solução*: Configurado `@CrossOrigin(origins = "*")` ao nível de controladores do Spring Boot para que simuladores móveis ou apps reativas em rede local ou Cloud Run obtenham livre intercâmbio de dados de catálogo.
3. **Mapeamento Circular Jackson no JPA**:
   * *Sintoma*: Relacionamento bi-direcional infinito ao converter entidades que possuíam coleções para formato de texto JSON.
   * *Solução*: Modularização estrita de carregamentos preguiçosos (*lazy load*) com respostas planas em formato de mapa estruturado ou DTOs explícitos (`AuthResponse`, `AuthRequest`).

---

## 🗄️ Estrutura DB (Modelagem Relacional)

A modelagem de dados foi desenhada em conformidade técnica da terceira forma normal (3FN) para implantação transparente em base relacional **PostgreSQL**:

```sql
-- TABELA DE UTILIZADORES DO ATELIÊ
CREATE TABLE suit_users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- TABELA DE CONTROLE DE PERMISSÕES (Relação 1-N com usuários)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES suit_users(id) ON DELETE CASCADE
);

-- TABELA DE REGISTO DE ENCOMENDAS SOB MEDIDA (Bespoke Suit Orders)
CREATE TABLE suit_orders (
    id VARCHAR(50) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    lapel_type VARCHAR(100) NOT NULL,
    fabric_color VARCHAR(7) NOT NULL, -- Hex code ex: #C8A96A
    lining_pattern_key VARCHAR(100) NOT NULL,
    fit_type VARCHAR(50) NOT NULL,
    
    -- Medidas Corporais do Cliente
    shoulders_size DOUBLE PRECISION NOT NULL,
    chest_size DOUBLE PRECISION NOT NULL,
    sleeves_size DOUBLE PRECISION NOT NULL,
    waist_size DOUBLE PRECISION NOT NULL,
    
    -- Controle Financeiro (M-Pesa Moçambique)
    mpesa_transaction_id VARCHAR(50) NOT NULL UNIQUE,
    payment_proof_url VARCHAR(255) DEFAULT NULL,
    payment_validated BOOLEAN NOT NULL DEFAULT FALSE,
    admin_notes VARCHAR(500) DEFAULT NULL,
    
    -- Gestão Industrial
    status_str VARCHAR(50) NOT NULL DEFAULT 'Design Concluído',
    created_at_date TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

## 📂 Mapa de Pastas do Projeto para Extração

Os arquivos criados para este projeto de backend encontram-se organizados no diretório do aplicativo `/src/architecture_classes/springboot_backend`. Para importá-los para um projeto de produção gerado no IntelliJ, basta mapeá-los para a estrutura típica do Maven ou Gradle em:

```text
src/
 └── main/
      ├── java/
      │    └── mz/ac/unizambeze/suitup/backend/
      │         ├── config/
      │         │    ├── SecurityConfig.java            <-- Configurações de rotas e política stateless
      │         │    ├── JwtTokenProvider.java          <-- Geração de chave HMAC e decodificação do JWT
      │         │    └── JwtAuthenticationFilter.java   <-- Filtro interceptador de requisições HTTPS
      │         │
      │         ├── controller/
      │         │    ├── AuthController.java            <-- POST /auth/register e POST /auth/login
      │         │    ├── ModelController.java           <-- GET /models catálogo de cortes
      │         │    └── SuitOrderController.java       <-- Encomendas, Multipart screenshot, Sync Batch
      │         │
      │         ├── dto/
      │         │    ├── AuthRequest.java               <-- DTO entrada de e-mail e palavra-passe
      │         │    ├── AuthResponse.java              <-- DTO retorno com token bearer
      │         │    └── RegisterRequest.java           <-- DTO cadastro de utilizador
      │         │
      │         ├── entity/
      │         │    ├── UserEntity.java                <-- Entidade utilizador mapeada no banco
      │         │    └── SuitOrderEntity.java           <-- Entidade do fato completo e status de alfaiataria
      │         │
      │         ├── repository/
      │         │    ├── UserRepository.java            <-- Interface de acesso a dados de usuário JPA
      │         │    └── SuitOrderRepository.java       <-- Interface de persistência da encomenda
      │         │
      │         └── service/
      │              ├── CustomUserDetailsService.java  <-- Carrega utilizador e privilégios para Spring Security
      │              ├── FileStorageService.java        <-- Upload físico, limpeza de nome e UUID para faturas
      │              └── SuitOrderService.java          <-- Regras de negócio e transações de lotes offline
      │
      └── resources/
           └── application.properties                   <-- Parâmetros de ligação do PostgreSQL e chaves de ambiente
```
