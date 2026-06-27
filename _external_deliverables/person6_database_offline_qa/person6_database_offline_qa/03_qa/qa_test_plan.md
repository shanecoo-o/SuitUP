# Plano de Testes de QA - SuitUP

## 1. Abordagem de Testes
O plano de testes visa garantir a robustez da aplicação mobile KMP, a estabilidade das queries locais em SQLite e a resiliência do fluxo de sincronização offline.

## 2. Escopo dos Testes

### Frontend Mobile
* Validação de fluxos de navegação e renderização do Editor 2D.
* Comportamento de estados vazios (Empty States).

### Database & SQLite
* Execução correta das queries SQLDelight.
* Persistência correta de tipos primitivos convertidos do PostgreSQL.

### Offline & Sincronização
* Isolamento de rede (Modo Avião) e escrita na fila local.
* Comportamento do mecanismo Store-and-Forward no retorno da rede.
