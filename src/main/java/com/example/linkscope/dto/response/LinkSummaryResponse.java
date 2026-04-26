package com.example.linkscope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkSummaryResponse {

    private UUID id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private boolean active;
    private long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
