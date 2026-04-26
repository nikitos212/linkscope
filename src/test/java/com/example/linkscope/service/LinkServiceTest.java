package com.example.linkscope.service;

import com.example.linkscope.config.AppProperties;
import com.example.linkscope.dto.request.CreateLinkRequest;
import com.example.linkscope.dto.response.LinkResponse;
import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.exception.AliasAlreadyExistsException;
import com.example.linkscope.mapper.LinkMapper;
import com.example.linkscope.repository.LinkRepository;
import com.example.linkscope.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;
    @Mock
    private ShortCodeGenerator shortCodeGenerator;
    @Mock
    private CacheService cacheService;

    private LinkService linkService;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.setBaseUrl("http://localhost:8080");

        linkService = new LinkService(
                linkRepository,
                shortCodeGenerator,
                new LinkMapper(),
                cacheService,
                appProperties
        );
    }

    @Test
    void createLinkShouldSucceedForGeneratedCode() {
        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://example.com/some/long/url")
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();

        when(shortCodeGenerator.generateUniqueCode()).thenReturn("abc1234");
        when(linkRepository.save(any(LinkEntity.class))).thenAnswer(invocation -> {
            LinkEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            return entity;
        });

        LinkResponse response = linkService.createLink(request);

        assertThat(response.getShortCode()).isEqualTo("abc1234");
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/r/abc1234");
        assertThat(response.getOriginalUrl()).isEqualTo("https://example.com/some/long/url");
        verify(cacheService).cacheLink(any(LinkEntity.class));
    }

    @Test
    void createLinkShouldFailWhenCustomAliasAlreadyExists() {
        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://example.com")
                .customAlias("my-link")
                .build();

        when(linkRepository.existsByShortCode("my-link")).thenReturn(true);

        assertThatThrownBy(() -> linkService.createLink(request))
                .isInstanceOf(AliasAlreadyExistsException.class)
                .hasMessageContaining("my-link");

        verify(linkRepository, never()).save(any());
    }
}
