package com.example.linkscope.service;

import com.example.linkscope.config.AppProperties;
import com.example.linkscope.entity.LinkEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private static final String KEY_PREFIX = "links:";

    private final RedisTemplate<String, LinkCacheValue> redisTemplate;
    private final AppProperties appProperties;

    public Optional<LinkCacheValue> getByShortCode(String shortCode) {
        try {
            LinkCacheValue value = redisTemplate.opsForValue().get(cacheKey(shortCode));
            return Optional.ofNullable(value);
        } catch (Exception ex) {
            log.warn("Redis read failed for shortCode={}", shortCode, ex);
            return Optional.empty();
        }
    }

    public void cacheLink(LinkEntity link) {
        LocalDateTime expiresAt = link.getExpiresAt();
        if (expiresAt != null && !expiresAt.isAfter(LocalDateTime.now())) {
            evict(link.getShortCode());
            return;
        }

        Duration ttl = resolveTtl(expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            evict(link.getShortCode());
            return;
        }

        LinkCacheValue payload = LinkCacheValue.builder()
                .id(link.getId())
                .originalUrl(link.getOriginalUrl())
                .shortCode(link.getShortCode())
                .active(link.getActive())
                .clickCount(link.getClickCount())
                .createdAt(link.getCreatedAt())
                .updatedAt(link.getUpdatedAt())
                .expiresAt(link.getExpiresAt())
                .build();

        try {
            redisTemplate.opsForValue().set(cacheKey(link.getShortCode()), payload, ttl);
        } catch (Exception ex) {
            log.warn("Redis write failed for shortCode={}", link.getShortCode(), ex);
        }
    }

    public void evict(String shortCode) {
        try {
            redisTemplate.delete(cacheKey(shortCode));
        } catch (Exception ex) {
            log.warn("Redis delete failed for shortCode={}", shortCode, ex);
        }
    }

    private Duration resolveTtl(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            return appProperties.getCache().getDefaultTtl();
        }
        return Duration.between(LocalDateTime.now(), expiresAt);
    }

    private String cacheKey(String shortCode) {
        return KEY_PREFIX + shortCode;
    }
}
