package com.example.linkscope.service;

import com.example.linkscope.dto.response.LinkStatsResponse;
import com.example.linkscope.entity.ClickEventEntity;
import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.mapper.ClickEventMapper;
import com.example.linkscope.repository.ClickEventRepository;
import com.example.linkscope.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private LinkRepository linkRepository;
    @Mock
    private ClickEventRepository clickEventRepository;

    private StatsService statsService;

    @BeforeEach
    void setUp() {
        statsService = new StatsService(linkRepository, clickEventRepository, new ClickEventMapper());
    }

    @Test
    void shouldReturnAggregatedStats() {
        UUID linkId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        LinkEntity link = LinkEntity.builder()
                .id(linkId)
                .shortCode("abc1234")
                .originalUrl("https://example.com")
                .active(true)
                .clickCount(152L)
                .createdAt(now.minusDays(7))
                .expiresAt(now.plusDays(7))
                .build();

        ClickEventEntity click = ClickEventEntity.builder()
                .id(UUID.randomUUID())
                .link(link)
                .clickedAt(now.minusMinutes(5))
                .ipAddress("203.0.113.10")
                .userAgent("JUnit")
                .referer("https://google.com")
                .build();

        when(linkRepository.findByShortCode("abc1234")).thenReturn(Optional.of(link));
        when(clickEventRepository.countDistinctIpByLinkId(linkId)).thenReturn(48L);
        when(clickEventRepository.findLastClickedAtByLinkId(linkId)).thenReturn(now.minusMinutes(5));
        when(clickEventRepository.findTop10ByLink_IdOrderByClickedAtDesc(linkId)).thenReturn(List.of(click));

        LinkStatsResponse response = statsService.getStatsByShortCode("abc1234");

        assertThat(response.getShortCode()).isEqualTo("abc1234");
        assertThat(response.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(response.getTotalClicks()).isEqualTo(152L);
        assertThat(response.getUniqueIpCount()).isEqualTo(48L);
        assertThat(response.getRecentClicks()).hasSize(1);
    }
}
