# Estratégia de Resolução de Conflitos - SuitUP

Este documento estabelece as regras de conciliação de dados quando ocorrem divergências entre o estado local (SQLite) e o servidor (PostgreSQL).

## Regras de Resolução

1. **Fonte da Verdade:** O Backend/Servidor é sempre a palavra final sobre o estado financeiro e de fabrico de uma encomenda.
2. **Uso de IDs do Cliente (Client-Generated ID):** A app gera UUIDs localmente. Se o utilizador criar um pedido offline e a sincronização falhar a meio, o reenvio usará o mesmo ID, evitando a duplicação no servidor (princípio da idempotência).
3. **Conflito de Medidas Simultâneas:** Se o cliente atualizar as suas medidas offline e, ao mesmo tempo, o Alfaiate ajustar as medidas no painel Admin:
   * **Regra:** O timestamp mais recente (`updated_at`) vence se a edição for do cliente. Se a alteração for do Alfaiate, a alteração do Alfaiate substitui a do cliente.
4. **ID de Transação M-Pesa Duplicado:** * **Regra:** O primeiro a registar-se com sucesso no PostgreSQL valida a restrição `UNIQUE`. Envios posteriores com o mesmo código são bloqueados pelo backend com erro `400 Bad Request`, forçando a app a alertar o utilizador para corrigir o dado.
5. **Divergência de Status no Tracking:** Se o utilizador estiver offline e o Admin alterar o status do pedido para `IN_PRODUCTION`, quando o utilizador voltar a ter rede, a app descarta o status local antigo e descarrega o status atual do servidor.
