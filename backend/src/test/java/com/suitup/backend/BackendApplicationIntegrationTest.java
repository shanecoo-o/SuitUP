package com.suitup.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class BackendApplicationIntegrationTest {

    private static final int EXPECTED_TABLE_COUNT = 12;

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("suitup_test")
            .withUsername("suitup_test")
            .withPassword("suitup_test");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoadsWithPostgres() {
        assertThat(flyway.info().current()).isNotNull();
    }

    @Test
    void appliesAllMigrationsAndCreatesCoreTables() {
        assertThat(flyway.info().current().getVersion().toString()).isEqualTo("5");

        Integer tableCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM information_schema.tables
            WHERE table_schema = 'public'
              AND table_name IN (
                  'roles',
                  'users',
                  'user_roles',
                  'uploaded_files',
                  'suit_models',
                  'orders',
                  'order_items',
                  'measurements',
                  'payments',
                  'order_status_history',
                  'payment_status_history',
                  'idempotency_keys'
              )
            """,
            Integer.class
        );

        assertThat(tableCount).isEqualTo(EXPECTED_TABLE_COUNT);
    }
}
