package com.example.linkscope.service;

import com.example.linkscope.entity.ClickEventEntity;
import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.exception.LinkExpiredException;
import com.example.linkscope.exception.LinkNotFoundException;
import com.example.linkscope.repository.ClickEventRepository;
import com.example.linkscope.repository.LinkRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final LinkRepository linkRepository;
    private final ClickEventRepository clickEventRepository;
    private final CacheService cacheService;

    @Transactional
    public String resolveRedirectUrl(String shortCode, HttpServletRequest request) {
        LinkEntity link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        if (!Boolean.TRUE.equals(link.getActive()) || isExpired(link)) {
            throw new LinkExpiredException(shortCode);
        }

        link.setClickCount((link.getClickCount() == null ? 0L : link.getClickCount()) + 1);
        link.setUpdatedAt(LocalDateTime.now());
        linkRepository.save(link);

        ClickEventEntity event = ClickEventEntity.builder()
                .link(link)
                .clickedAt(LocalDateTime.now())
                .ipAddress(resolveIpAddress(request))
                .userAgent(request.getHeader("User-Agent"))
                .referer(request.getHeader("Referer"))
                .build();

        clickEventRepository.save(event);
        cacheService.cacheLink(link);

        return link.getOriginalUrl();
    }

    private boolean isExpired(LinkEntity link) {
        return link.getExpiresAt() != null && !link.getExpiresAt().isAfter(LocalDateTime.now());
    }

    private String resolveIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
