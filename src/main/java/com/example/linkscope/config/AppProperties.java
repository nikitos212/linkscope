package com.example.linkscope.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String baseUrl = "http://localhost:8080";
    private Cache cache = new Cache();

    @Data
    public static class Cache {
        private Duration defaultTtl = Duration.ofHours(24);
    }
}
