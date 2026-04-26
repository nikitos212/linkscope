package com.example.linkscope.service;

import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.exception.LinkExpiredException;
import com.example.linkscope.repository.ClickEventRepository;
import com.example.linkscope.repository.LinkRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectServiceTest {

    @Mock
    private LinkRepository linkRepository;
    @Mock
    private ClickEventRepository clickEventRepository;
    @Mock
    private CacheService cacheService;
    @Mock
    private HttpServletRequest httpServletRequest;

    private RedirectService redirectService;

    @BeforeEach
    void setUp() {
        redirectService = new RedirectService(linkRepository, clickEventRepository, cacheService);
    }

    @Test
    void shouldThrowGoneForExpiredLink() {
        LinkEntity expiredLink = LinkEntity.builder()
                .id(UUID.randomUUID())
                .shortCode("abc1234")
                .originalUrl("https://example.com")
                .active(true)
                .clickCount(0L)
                .createdAt(LocalDateTime.now().minusDays(1))
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(linkRepository.findByShortCode("abc1234")).thenReturn(Optional.of(expiredLink));

        assertThatThrownBy(() -> redirectService.resolveRedirectUrl("abc1234", httpServletRequest))
                .isInstanceOf(LinkExpiredException.class);

        verify(linkRepository, never()).save(expiredLink);
        verify(clickEventRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
