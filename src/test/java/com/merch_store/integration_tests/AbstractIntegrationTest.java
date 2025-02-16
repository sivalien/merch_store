package com.merch_store.integration_tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    static final String IMAGE_NAME = "postgres:13";

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(IMAGE_NAME)
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        POSTGRESQL_CONTAINER.start();
    }

    @BeforeAll
    static void beforeAll() {
        System.setProperty("spring.datasource.url", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRESQL_CONTAINER.getPassword());
    }

//    @AfterAll
//    static void afterAll() {
//        POSTGRESQL_CONTAINER.stop();
//    }
}
