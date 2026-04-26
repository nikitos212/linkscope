package com.example.linkscope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkStatsResponse {

    private String shortCode;
    private String originalUrl;
    private long totalClicks;
    private long uniqueIpCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastClickedAt;
    private List<ClickEventResponse> recentClicks;
}
