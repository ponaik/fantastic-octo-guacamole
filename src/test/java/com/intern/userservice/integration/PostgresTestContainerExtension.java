package com.intern.userservice.integration;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresTestContainerExtension implements BeforeAllCallback {

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:18-alpine"))
                    .withDatabaseName("test")
                    .withUsername("duke")
                    .withPassword("s3cret")
                    .withReuse(true);

    static {
        POSTGRES.start();

        System.setProperty("spring.datasource.url", POSTGRES.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES.getUsername());
        System.setProperty("spring.datasource.password", POSTGRES.getPassword());
        System.setProperty("spring.jpa.hibernate.ddl-auto", "none");
    }

    @Override
    public void beforeAll(ExtensionContext context) {

    }
}
