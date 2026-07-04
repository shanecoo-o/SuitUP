# QA Checklist - SuitUP

- [ ] App instala no Android físico sem erros de empacotamento.
- [ ] App abre sem crash imediato.
- [ ] Ecrã de Login valida credenciais locais.
- [ ] Catálogo de modelos renderiza as imagens corretamente.
- [ ] Editor 2D permite selecionar estilos e calcula o preço estimado.
- [ ] Carrinho vazio mostra o estado adequado (Empty State).
- [ ] Pedidos vazios mostram o estado adequado (Empty State).
- [ ] Checkout abre e herda o perfil de medidas correto.
- [ ] Upload de comprovativo simula envio local em background.
- [ ] Pedido criado offline exibe indicador visual de "Pendente de Sincronização".
- [ ] Transições de rede acionam automaticamente o Ktor Client para esvaziar a `sync_queue`.
- [ ] Backend rejeita transações M-Pesa duplicadas.
- [ ] Atualizações de status efetuadas pelo Admin refletem no ecrã de rastreio em menos de 5 segundos.
