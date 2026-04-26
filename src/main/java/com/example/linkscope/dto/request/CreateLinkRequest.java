package com.example.linkscope.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CreateLinkRequest {

    @NotBlank(message = "originalUrl is required")
    @URL(message = "originalUrl must be a valid URL")
    private String originalUrl;

    @Size(min = 3, max = 30, message = "customAlias length must be between 3 and 30")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "customAlias allows only letters, digits, hyphen and underscore")
    private String customAlias;

    @Future(message = "expiresAt must be in the future")
    private LocalDateTime expiresAt;
}
