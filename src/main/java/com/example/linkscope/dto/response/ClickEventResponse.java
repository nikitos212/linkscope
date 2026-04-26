package com.example.linkscope.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickEventResponse {

    private LocalDateTime clickedAt;
    private String ipAddress;
    private String userAgent;
    private String referer;
}
