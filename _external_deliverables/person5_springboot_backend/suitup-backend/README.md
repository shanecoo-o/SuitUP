# 👔 SuitUP Backend — Spring Boot & PostgreSQL Service

Bem-vindo ao serviço de backend industrial da **SuitUP**! Este ecossistema foi projetado utilizando **Spring Boot 3.x**, **Spring Data JPA**, **Spring Security 6.x** e **PostgreSQL** para fornecer APIs estáveis, rastreamento físico de produção de fatos sob medida, auditoria contra duplicidades de transações do M-Pesa e lote (`sync-batch`) resiliente para o aplicativo móvel desenvolvido em **Kotlin Multiplatform (KMP)**.

---

## 🚀 Como Executar o Backend Localmente

### 📋 Pré-requisitos
Antes de iniciar, certifique-se de ter instalado na sua máquina de desenvolvimento:
* **Java Development Kit (JDK 17 ou 21)**
* **Maven 3.8+** (Opcional, pois pode usar o Maven Wrapper fornecido)
* **PostgreSQL 14+** (Ativo localmente na porta padrão `5432`)
* **IntelliJ IDEA** (Ultimate ou Community com Plugins Spring integrados)

---

## 🗄️ 1. Configurando a Base de Dados (PostgreSQL)

Para que as tabelas de cadastros de usuários e encomendas sejam auto-geradas sem falhas do Hibernate, certifique-se de executar o comando abaixo utilizando o utilitário **pgAdmin** ou o terminal **psql**:

```sql
-- Execute este comando para criar a base de dados reservada do projeto
CREATE DATABASE suitup_db ENCODING 'UTF8';
```

---

## ⚙️ 2. Propriedades de Conexão

As credenciais padrão de conexão encontram-se descritas no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/suitup_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```
*Se a palavra-passe do seu PostgreSQL local for diferente, atualize o valor de `spring.datasource.password` antes do boot.*

---

## 🏗️ 3. Compilando e Executando a Aplicação

Navegue até a pasta raiz do projeto (`suitup-backend/`) e execute os comandos abaixo no terminal:

```bash
# Limpar rastros anteriores e compilar o código gerando o arquivo JAR
mvn clean install

# Inicializar o servidor embutido Apache Tomcat na porta 8080
mvn spring-boot:run
```

A seguir, verá logs detalhados confirmando que as tabelas `suit_users` e `suit_orders` foram devidamente provisionadas, e o servidor estará ativo para requisições no endereço:
👉 `http://localhost:8080`

---

## 🗺️ 4. Lista Completa de Endpoints REST (Porta `8080`)

### 🔑 APIs de Autenticação (`/api/auth/*`)
* **`POST /api/auth/register`**: Cadastra um novo perfil de usuário (`CLIENT`, `ADMIN`, `TAILOR`) encriptando a palavra-passe por meio de hash BCrypt.
* **`POST /api/auth/login`**: Valida credenciais e retorna metadados com JWT.
* **`GET /api/me`**: Retorna os detalhes de perfil do portador da chave ativa atual.

### 🧵 APIs de Encomendas (`/api/orders/*`)
* **`POST /api/orders`**: Cria um novo pedido de terno personalizado sob medida.
  * *Valida ID de transação M-Pesa para impedir duplicações criminosas ou acidentais.*
* **`GET /api/orders`**: Lista geral de todos os fatos sob medida e canais de sincronização inseridos no sistema.
* **`GET /api/orders/{id}`**: Detalhes completos e rastreamento (tracking steps) de um pedido por ID.
* **`POST /api/orders/{id}/payment-proof`**: Envia via Multipart Form File (até 10MB) a foto física de recibo m-pesa (`.png`, `.jpg`, `.pdf`).
* **`GET /api/orders/{id}/payment-proof`**: Retorna metadados do documento associado àquela transação.

### 🛡️ APIs de Administração e Alfaiates (`/api/orders/{id}/*`)
* **`PUT /api/orders/{id}/validate-payment`**: Para conciliação financeira de equipe de administração.
  * Enviar `{"approved": true}` para colocar a fatura como `VALIDATED` e mover ordem operacional para `PAYMENT_VALIDATED`.
* **`PUT /api/orders/{id}/status`**: Usado por Alfaiates na confecção do terno.
  * Modifica estados para `IN_PRODUCTION`, `READY_FOR_PICKUP` ou `DELIVERED`.

---

## 📳 5. Integração com Sincronização em Lote Offline (`sync-batch`)

No aplicativo Kotlin Multiplatform, se o usuário solicitar e concluir pedidos enquanto estiver em trânsito e sem acesso à internet, o app guardará localmente as encomendas gerando IDs provisórios (`clientGeneratedId`).

Quando a conexão retornar, o aplicativo fará uma única requisição POST para:
👉 `POST /api/orders/sync-batch`

### 🛡️ Regra Anti-Duplicidade do Lote
O backend executa os seguintes passos para garantir proteção total dos dados:
1. Avalia cada registro do lote.
2. Se o ID do M-Pesa (`mpesaTransactionId`) ou o ID local (`clientGeneratedId`) já existir de sincronizações anteriores, o elemento será ignorado com sucesso (registrado como `ignoredIds`).
3. Somente registros legítimos inéditos serão inseridos na tabela PostgreSQL.
4. O retorno do lote é um sumário de processamento:
   ```json
   {
     "status": "COMPLETED",
     "totalProcessed": 3,
     "totalSynced": 2,
     "totalIgnored": 1,
     "syncedIds": ["SUIT-SYNC-uuid-1", "SUIT-SYNC-uuid-2"],
     "ignoredIds": ["uuid-antigo-repetido"]
   }
   ```

---

## 🛠️ 6. Abrindo o Projeto no IntelliJ IDEA

1. Abra o IntelliJ IDEA.
2. Selecione **Open** e navegue até a pasta `suitup-backend/`.
3. Escolha o arquivo `pom.xml` para abrir como um projeto Maven.
4. Aguarde a sincronização e indexação das dependências.
5. Localize a classe `com.suitup.backend.SuitUpBackendApplication` e execute o ícone verde **Run**.
