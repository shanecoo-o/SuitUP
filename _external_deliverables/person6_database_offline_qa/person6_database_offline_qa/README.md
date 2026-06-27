# SuitUP - Camada de Dados, Estratégia Offline e Garantia de Qualidade

Este repositório contém os entregáveis da **Pessoa 6**, cobrindo o desenho da base de dados, a arquitetura offline-first para o ecossistema Kotlin Multiplatform (KMP) e o planeamento completo de QA.

## Estrutura da Pasta
* `01_database/`: Contém os schemas SQL PostgreSQL, sementes de dados e o dicionário explicativo.
* `02_offline/`: Arquitetura de filas de sincronização e o desenho do banco SQLite local.
* `03_qa/`: Casos de teste, checklists e templates de reporte de bugs.

## Como Executar os Testes de Schema Locais
1. Para o PostgreSQL: Copiar os conteúdos de `schema_postgresql.sql` e rodar na Query Tool do pgAdmin 4.
2. Para o SQLite Local: Usar o portal `sqliteonline.com` para compilar o `local_sqlite_schema.sql`.
