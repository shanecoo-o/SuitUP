# Relatório de Garantia de Qualidade (QA) - Project SuitUP

## Resumo Executivo
Este documento detalha o plano de testes de integração e validação das regras de negócio do backend e da base de dados. As métricas de aprovação e evidências físicas serão preenchidas pelos engenheiros de QA após a execução prática em ambiente de *staging*.

---

## 1. Testes Planeados: Fluxo de Sincronização Distribuída (SBDD)

### Cenário 1.1: Inserção de pedido offline na `sync_queue`
* **Descrição:** Garantir que um pedido criado por um alfaiate sem ligação à internet é registado apenas localmente e adicionado à fila de sincronização.
* **Testes Previstos:**
    * Desconectar a rede do dispositivo de teste.
    * Registar um novo fato e respetivas medidas no perfil de um cliente.
    * Verificar a inserção do registo na tabela local `local_orders`.
    * Verificar a criação do *payload* na tabela `sync_queue` com o estado `PENDING`.
* **Critérios de Aceitação:** A transação local não deve falhar devido à ausência de ligação com a base de dados central. O ficheiro JSONB gerado deve conter todos os dados do pedido.
* **Estado:** ⏳ Aguardando execução final.

### Cenário 1.2: Resolução da `sync_queue` após restabelecimento de rede
* **Descrição:** Validar o envio e a purga dos dados assim que o dispositivo retoma a ligação ao servidor central.
* **Testes Previstos:**
    * Reconectar o dispositivo à rede.
    * Disparar o *job* de sincronização.
    * Confirmar a atualização do estado na `sync_queue` de `PENDING` para `SYNCED`.
    * Executar a query de limpeza (`deleteSyncedQueueItem`) e confirmar a remoção da linha.
* **Critérios de Aceitação:** O ID universal (UUID) gerado no nó local deve ser exatamente o mesmo refletido no servidor central, sem duplicação de dados.
* **Estado:** ⏳ Aguardando execução final.

---

## 2. Testes Planeados: Integridade de Transações e Regras de Negócio

### Cenário 2.1: Bloqueio de avanço de estado sem validação de pagamento
* **Descrição:** Impedir que o estado de produção avance se o comprovativo de pagamento (M-Pesa) estiver pendente ou rejeitado.
* **Testes Previstos:**
    * Tentar fazer um `UPDATE` no `order_status` para `IN_PRODUCTION` num pedido cujo `payment_status` seja `PENDING`.
* **Critérios de Aceitação:** O SGBD ou a camada de serviço deve rejeitar a operação e devolver um erro de transação (Constraint Violation), abortando o processo (*Rollback*).
* **Estado:** ⏳ Aguardando execução final.

### Cenário 2.2: Automação temporal via Triggers (ECA)
* **Descrição:** Garantir que a coluna de auditoria `updated_at` reflete o momento exato de qualquer modificação num pedido.
* **Testes Previstos:**
    * Consultar a data atual do `updated_at` do pedido `ORD-2026-001`.
    * Atualizar o endereço de entrega na tabela `delivery_info`.
    * Consultar novamente o registo e comparar as datas.
* **Critérios de Aceitação:** O valor de `updated_at` deve ser obrigatoriamente superior à data de criação, refletindo o `CURRENT_TIMESTAMP` do momento do `UPDATE`.
* **Estado:** ⏳ Aguardando execução final.

---

## 3. Secção de Registo de Evidências (Template para a Equipa de QA)
*(A preencher após a execução de cada bateria de testes)*

| Cenário | Dispositivo Usado | SO / Versão | Data do Teste | Versão da App | Observações / Link de Logs |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1.1 | - | - | - | - | - |
| 1.2 | - | - | - | - | - |
| 2.1 | - | - | - | - | - |
| 2.2 | - | - | - | - | - |

**Anexos Exigidos para Aprovação:**
* *Screenshots* do dispositivo móvel confirmando os *prompts* de erro ou sucesso.
* Ficheiro de log de rede (exportado via Postman ou terminal).
* *Logs* de erro do PostgreSQL em caso de falha de *Constraints*.
