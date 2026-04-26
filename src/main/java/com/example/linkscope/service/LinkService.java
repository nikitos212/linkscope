package com.example.linkscope.service;

import com.example.linkscope.config.AppProperties;
import com.example.linkscope.dto.request.CreateLinkRequest;
import com.example.linkscope.dto.request.UpdateLinkRequest;
import com.example.linkscope.dto.response.LinkResponse;
import com.example.linkscope.dto.response.LinkSummaryResponse;
import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.exception.AliasAlreadyExistsException;
import com.example.linkscope.exception.LinkNotFoundException;
import com.example.linkscope.mapper.LinkMapper;
import com.example.linkscope.repository.LinkRepository;
import com.example.linkscope.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final LinkMapper linkMapper;
    private final CacheService cacheService;
    private final AppProperties appProperties;

    @Transactional
    public LinkResponse createLink(CreateLinkRequest request) {
        validateExpiresAt(request.getExpiresAt());

        String shortCode = resolveShortCode(request.getCustomAlias());

        LinkEntity entity = LinkEntity.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .active(true)
                .clickCount(0L)
                .expiresAt(request.getExpiresAt())
                .build();

        try {
            LinkEntity saved = linkRepository.save(entity);
            cacheService.cacheLink(saved);
            return linkMapper.toResponse(saved, buildShortUrl(saved.getShortCode()));
        } catch (DataIntegrityViolationException ex) {
            throw new AliasAlreadyExistsException(shortCode);
        }
    }

    @Transactional(readOnly = true)
    public LinkResponse getLinkByShortCode(String shortCode) {
        return cacheService.getByShortCode(shortCode)
                .map(cached -> linkMapper.toResponse(cached, buildShortUrl(cached.getShortCode())))
                .orElseGet(() -> {
                    LinkEntity entity = getEntityByShortCode(shortCode);
                    cacheService.cacheLink(entity);
                    return linkMapper.toResponse(entity, buildShortUrl(entity.getShortCode()));
                });
    }

    @Transactional
    public LinkResponse updateLink(String shortCode, UpdateLinkRequest request) {
        validateExpiresAt(request.getExpiresAt());

        LinkEntity entity = getEntityByShortCode(shortCode);

        if (StringUtils.hasText(request.getOriginalUrl())) {
            entity.setOriginalUrl(request.getOriginalUrl());
        }
        if (request.getExpiresAt() != null) {
            entity.setExpiresAt(request.getExpiresAt());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }

        entity.setUpdatedAt(LocalDateTime.now());
        LinkEntity updated = linkRepository.save(entity);
        cacheService.cacheLink(updated);

        return linkMapper.toResponse(updated, buildShortUrl(updated.getShortCode()));
    }

    @Transactional
    public void softDeleteLink(String shortCode) {
        LinkEntity entity = getEntityByShortCode(shortCode);
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        linkRepository.save(entity);
        cacheService.evict(shortCode);
    }

    @Transactional(readOnly = true)
    public Page<LinkSummaryResponse> getLinks(Pageable pageable) {
        return linkRepository.findAll(pageable)
                .map(link -> linkMapper.toSummary(link, buildShortUrl(link.getShortCode())));
    }

    @Transactional(readOnly = true)
    public LinkEntity getEntityByShortCode(String shortCode) {
        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));
    }

    private String resolveShortCode(String customAlias) {
        if (StringUtils.hasText(customAlias)) {
            String alias = customAlias.trim();
            if (linkRepository.existsByShortCode(alias)) {
                throw new AliasAlreadyExistsException(alias);
            }
            return alias;
        }
        return shortCodeGenerator.generateUniqueCode();
    }

    private String buildShortUrl(String shortCode) {
        String baseUrl = appProperties.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/r/" + shortCode;
    }

    private void validateExpiresAt(LocalDateTime expiresAt) {
        if (expiresAt != null && !expiresAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }
    }
}
