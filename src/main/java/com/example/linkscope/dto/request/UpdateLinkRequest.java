package com.example.linkscope.dto.request;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLinkRequest {

    @URL(message = "originalUrl must be a valid URL")
    private String originalUrl;

    @Future(message = "expiresAt must be in the future")
    private LocalDateTime expiresAt;

    private Boolean active;
}
