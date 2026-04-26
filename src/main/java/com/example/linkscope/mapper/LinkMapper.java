package com.example.linkscope.mapper;

import com.example.linkscope.dto.response.LinkResponse;
import com.example.linkscope.dto.response.LinkSummaryResponse;
import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.service.LinkCacheValue;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {

    public LinkResponse toResponse(LinkEntity entity, String shortUrl) {
        return LinkResponse.builder()
                .id(entity.getId())
                .originalUrl(entity.getOriginalUrl())
                .shortCode(entity.getShortCode())
                .shortUrl(shortUrl)
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .active(Boolean.TRUE.equals(entity.getActive()))
                .clickCount(entity.getClickCount() == null ? 0L : entity.getClickCount())
                .build();
    }

    public LinkResponse toResponse(LinkCacheValue cached, String shortUrl) {
        return LinkResponse.builder()
                .id(cached.getId())
                .originalUrl(cached.getOriginalUrl())
                .shortCode(cached.getShortCode())
                .shortUrl(shortUrl)
                .createdAt(cached.getCreatedAt())
                .expiresAt(cached.getExpiresAt())
                .active(Boolean.TRUE.equals(cached.getActive()))
                .clickCount(cached.getClickCount() == null ? 0L : cached.getClickCount())
                .build();
    }

    public LinkSummaryResponse toSummary(LinkEntity entity, String shortUrl) {
        return LinkSummaryResponse.builder()
                .id(entity.getId())
                .shortCode(entity.getShortCode())
                .shortUrl(shortUrl)
                .originalUrl(entity.getOriginalUrl())
                .active(Boolean.TRUE.equals(entity.getActive()))
                .clickCount(entity.getClickCount() == null ? 0L : entity.getClickCount())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}
