# Casos de Teste de QA - SuitUP (25 Casos Oficiais)

## Grupo 1: Navegação (5 Testes)
### TC-001 - Abrir Aplicação
* **Pré-condição:** App instalada.
* **Passos:** Tocar no ícone da app.
* **Resultado Esperado:** Splash screen e ecrã de Login abrem com sucesso.
* **Status:** Pass

### TC-002 - Acesso ao Catálogo
* **Pré-condição:** Utilizador autenticado.
* **Passos:** Clicar na aba "Modelos".
* **Resultado Esperado:** Modelos de fatos carregam com imagens e preços base.
* **Status:** Pass

### TC-003 - Entrada no Editor 2D
* **Pré-condição:** Catálogo carregado.
* **Passos:** Selecionar o modelo "Modern Slim Fit" e clicar em Personalizar.
* **Resultado Esperado:** Interface do editor abre com todas as opções visíveis.
* **Status:** Pass

### TC-004 - Histórico de Pedidos Vazio
* **Pré-condição:** Utilizador sem compras anteriores.
* **Passos:** Clicar em "Meus Pedidos".
* **Resultado Esperado:** Exibe imagem/texto descritivo de "Nenhum pedido efetuado".
* **Status:** Pass

### TC-005 - Carrinho Vazio
* **Pré-condição:** Sem itens selecionados.
* **Passos:** Clicar no ícone do Carrinho.
* **Resultado Esperado:** Exibe mensagem de "Carrinho Vazio".
* **Status:** Pass

## Grupo 2: Pedidos (5 Testes)
### TC-006 - Configuração de Estilo no Editor
* **Pré-condição:** Editor aberto.
* **Passos:** Mudar o tipo de lapela e o tecido.
* **Resultado Esperado:** Preço estimado atualiza no rodapé de forma dinâmica.
* **Status:** Pass

### TC-007 - Salvar Perfil de Medidas
* **Pré-condição:** Formulário de medidas aberto.
* **Passos:** Preencher dados obrigatórios (ombros, peito) e guardar com a label "Casamento 2026".
* **Resultado Esperado:** Perfil guardado e listado com sucesso.
* **Status:** Pass

### TC-008 - Criação de Pedido com Dados Completos
* **Pré-condição:** Design e medidas associados.
* **Passos:** Avançar para o ecrã de resumo e submeter.
* **Resultado Esperado:** Código de pedido único (ORDXXXX) gerado e exibido.
* **Status:** Pass

### TC-009 - Persistência das Chaves Estrangeiras
* **Pré-condição:** Pedido submetido com sucesso.
* **Passos:** Validar registo na tabela `orders`.
* **Resultado Esperado:** IDs de usuário, design e medidas preenchidos obrigatoriamente (`NOT NULL`).
* **Status:** Pass

### TC-010 - Seleção de Tipo de Recolha (Pickup)
* **Pré-condição:** Ecrã de checkout ativo.
* **Passos:** Escolher opção "Levantamento na Loja" e selecionar o ponto de recolha.
* **Resultado Esperado:** Tabela `delivery_info` grava a loja e limpa campos de endereço residencial.
* **Status:** Pass

## Grupo 3: Checkout e Pagamento (5 Testes)
### TC-011 - Submissão de Código M-Pesa Único
* **Pré-condição:** Checkout pendente de pagamento.
* **Passos:** Digitar um ID de transação inédito e enviar comprovativo.
* **Resultado Esperado:** O status passa para `PROOF_UPLOADED`.
* **Status:** Pass

### TC-012 - Rejeição de Código M-Pesa Duplicado
* **Pré-condição:** Código M-Pesa já existente no banco.
* **Passos:** Tentar submeter o mesmo código num segundo pedido.
* **Resultado Esperado:** O banco rejeita a inserção devido à restrição `UNIQUE`.
* **Status:** Pass

### TC-013 - Upload de Ficheiro Válido
* **Pré-condição:** Ecrã de upload aberto.
* **Passos:** Selecionar imagem JPG de comprovativo.
* **Resultado Esperado:** Caminho (`file_path`) salvo corretamente na tabela `payment_proofs`.
* **Status:** Pass

### TC-014 - Validação de Pagamento pelo Admin
* **Pré-condição:** Pedido com status `PROOF_UPLOADED`.
* **Passos:** Admin clica em "Validar" no backend.
* **Resultado Esperado:** `payment_status` muda para `VALIDATED` e `order_status` passa para `PAYMENT_VALIDATED`.
* **Status:** Pass

### TC-015 - Recusa de Pagamento por Comprovativo Inválido
* **Pré-condição:** Pedido com status `PROOF_UPLOADED`.
* **Passos:** Admin recusa e adiciona nota "Imagem ilegível".
* **Resultado Esperado:** Status do pagamento muda para `REJECTED` e notas de validação são guardadas.
* **Status:** Pass

## Grupo 4: Offline e Sincronização (5 Testes)
### TC-016 - Criação de Pedido Sem Internet
* **Pré-condição:** Modo Avião ativado no telemóvel.
* **Passos:** Finalizar a encomenda de um terno.
* **Resultado Esperado:** Guardado na tabela `local_orders` com `sync_pending = 1`.
* **Status:** Pass

### TC-017 - Alimentação da Fila de Sincronização
* **Pré-condição:** Pedido gerado em modo offline.
* **Passos:** Inspecionar a tabela `sync_queue`.
* **Resultado Esperado:** Nova linha inserida com o JSON do pedido no payload e status `PENDING`.
* **Status:** Pass

### TC-018 - Recuperação de Rede Automática
* **Pré-condição:** Pedido na fila local pendente.
* **Passos:** Desativar o Modo Avião (restaurar internet).
* **Resultado Esperado:** O Ktor detecta a rede e inicia o envio do payload em background.
* **Status:** Pass

### TC-019 - Atualização Pós-Sincronização
* **Pré-condição:** Resposta positiva `201 Created` enviada pelo Spring Boot.
* **Passos:** Verificar base local.
* **Resultado Esperado:** `sync_pending` alterado para `0` e o `server_id` definitivo é gravado na tabela local.
* **Status:** Pass

### TC-020 - Resiliência a Falha de Envio (Retry)
* **Pré-condição:** Sincronização iniciada, mas a internet cai a meio.
* **Passos:** Aguardar interrupção da chamada.
* **Resultado Esperado:** `retry_count` incrementado para 1, status mantido como `PENDING` para nova tentativa.
* **Status:** Pass

## Grupo 5: Estados de Erro e Casos de Borda (5 Testes)
### TC-021 - Validação de E-mail Único no Registo
* **Pré-condição:** Formulário de registo aberto.
* **Passos:** Tentar registar um e-mail já existente.
* **Resultado Esperado:** Sistema lança erro de violação de constraint UNIQUE e impede o registo.
* **Status:** Pass

### TC-022 - Tentativa de Checkout com Medidas Zeradas
* **Pré-condição:** Carrinho com itens, mas sem perfil de medidas associado.
* **Passos:** Clicar em Finalizar Compra.
* **Resultado Esperado:** Validação bloqueia e exige a criação/seleção de medidas válidas.
* **Status:** Pass

### TC-023 - Tentativa de Inserção de Modelo de Terno Inativo
* **Pré-condição:** Modelo com `active = false`.
* **Passos:** Enviar requisição direta via API tentando comprar o modelo.
* **Resultado Esperado:** Backend bloqueia a transação com erro de validação de catálogo.
* **Status:** Pass

### TC-024 - Histórico de Status Duplicado
* **Pré-condição:** Pedido existente.
* **Passos:** Tentar inserir dois estados idênticos ao mesmo tempo.
* **Resultado Esperado:** O campo `created_at` diferencia as entradas na linha do tempo ordenando de forma correta por timestamp.
* **Status:** Pass

### TC-025 - Idempotência em Duplo Envio da Fila
* **Pré-condição:** Instabilidade de rede provoca reenvio de payload idêntico.
* **Passos:** App envia o mesmo UUID de pedido que o servidor já salvou.
* **Resultado Esperado:** O Spring Boot ignora o insert e devolve `200 OK`, saneando a fila local sem criar duplicados.
* **Status:** Pass
