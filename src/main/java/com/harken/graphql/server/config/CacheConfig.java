package com.harken.graphql.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix ="caching")
@Data
public class CacheConfig {

    private Map<String, CacheSpecification> config;

    @Bean Ticker ticker() {
        return Ticker.systemTicker();
    }

    @Data
    public static class CacheSpecification {
        private Integer ttl;
        private Integer max;
    }

    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        SimpleCacheManager manager = new SimpleCacheManager();
        if (config != null) {
            List<CaffeineCache> caches = config.entrySet().stream()
                    .map(entry -> createCache(entry.getKey(), entry.getValue(), ticker))
                    .collect(Collectors.toList());
            manager.setCaches(caches);
        }
        return manager;
    }

    private CaffeineCache createCache(String name, CacheSpecification config, Ticker ticker) {
        final @NonNull Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(config.getTtl().equals(-1) ? Long.MAX_VALUE : config.getTtl(), TimeUnit.MINUTES)
                .maximumSize(config.getMax())
                .ticker(ticker);

        return new CaffeineCache(name, caffeineBuilder.build());
    }

}
