package com.suitup.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASSE PRINCIPAL DE BOOTSTRAP (Spring Boot Context)
 * Responsável por inicializar e configurar todos os componentes, serviços,
 * filtros do Spring Security e conexões de Banco de Dados.
 */
@SpringBootApplication
public class SuitUpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuitUpBackendApplication.class, args);
        System.out.println("=======================================================================");
        System.out.println("   SUITUP BACKEND SPRING BOOT INICIADO COM SUCESSO EM http://localhost:8080 ");
        System.out.println("   A ligar ao PostgreSQL e pronto para chamadas de APIs e Sincronização.");
        System.out.println("=======================================================================");
    }
}
