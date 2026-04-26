package com.example.linkscope.service;

import com.example.linkscope.dto.response.LinkStatsResponse;
import com.example.linkscope.entity.LinkEntity;
import com.example.linkscope.exception.LinkNotFoundException;
import com.example.linkscope.mapper.ClickEventMapper;
import com.example.linkscope.repository.ClickEventRepository;
import com.example.linkscope.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final LinkRepository linkRepository;
    private final ClickEventRepository clickEventRepository;
    private final ClickEventMapper clickEventMapper;

    @Transactional(readOnly = true)
    public LinkStatsResponse getStatsByShortCode(String shortCode) {
        LinkEntity link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        return LinkStatsResponse.builder()
                .shortCode(link.getShortCode())
                .originalUrl(link.getOriginalUrl())
                .totalClicks(link.getClickCount() == null ? 0L : link.getClickCount())
                .uniqueIpCount(clickEventRepository.countDistinctIpByLinkId(link.getId()))
                .createdAt(link.getCreatedAt())
                .expiresAt(link.getExpiresAt())
                .lastClickedAt(clickEventRepository.findLastClickedAtByLinkId(link.getId()))
                .recentClicks(clickEventRepository.findTop10ByLink_IdOrderByClickedAtDesc(link.getId())
                        .stream()
                        .map(clickEventMapper::toResponse)
                        .toList())
                .build();
    }
}
