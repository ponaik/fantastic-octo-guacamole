package com.intern.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    private final CacheTtlProperties cacheTtlProperties;

    public RedisCacheConfig(CacheTtlProperties cacheTtlProperties) {
        this.cacheTtlProperties = cacheTtlProperties;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(cacheTtlProperties.getDefaults());

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheTtlProperties.getCacheNames().forEach((name, ttl) ->
                cacheConfigs.put(name, defaultConfig.entryTtl(ttl))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}