package com.example.linkscope.mapper;

import com.example.linkscope.dto.response.ClickEventResponse;
import com.example.linkscope.entity.ClickEventEntity;
import org.springframework.stereotype.Component;

@Component
public class ClickEventMapper {

    public ClickEventResponse toResponse(ClickEventEntity entity) {
        return ClickEventResponse.builder()
                .clickedAt(entity.getClickedAt())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .referer(entity.getReferer())
                .build();
    }
}
