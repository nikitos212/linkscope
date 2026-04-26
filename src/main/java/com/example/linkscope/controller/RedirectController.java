package com.example.linkscope.controller;

import com.example.linkscope.service.RedirectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "Redirect", description = "Public redirect endpoint")
public class RedirectController {

    private final RedirectService redirectService;

    @GetMapping("/r/{shortCode}")
    @Operation(summary = "Redirect by short code", description = "Redirects to original URL and writes click event")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = redirectService.resolveRedirectUrl(shortCode, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(originalUrl).toString())
                .build();
    }
}
