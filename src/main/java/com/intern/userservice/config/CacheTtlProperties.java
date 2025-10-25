package com.intern.userservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cache.ttl")
public class CacheTtlProperties {

    private Duration defaults = Duration.ofMinutes(10);
    private Map<String, Duration> cacheNames = new HashMap<>();
}
