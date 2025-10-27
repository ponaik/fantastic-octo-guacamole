package com.intern.userservice.integration.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class RedisTestContainerExtension implements BeforeAllCallback {

    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:8.2.2-alpine")
                    .withExposedPorts(6379)
                    .withReuse(true);

    static {
        REDIS.start();

        System.setProperty("spring.data.redis.host", REDIS.getHost());
        System.setProperty("spring.data.redis.port", REDIS.getMappedPort(6379).toString());
    }

    @Override
    public void beforeAll(ExtensionContext context) {

    }
}
