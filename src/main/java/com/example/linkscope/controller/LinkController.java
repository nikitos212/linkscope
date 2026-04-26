package com.example.linkscope.controller;

import com.example.linkscope.dto.request.CreateLinkRequest;
import com.example.linkscope.dto.request.UpdateLinkRequest;
import com.example.linkscope.dto.response.LinkResponse;
import com.example.linkscope.dto.response.LinkStatsResponse;
import com.example.linkscope.dto.response.LinkSummaryResponse;
import com.example.linkscope.service.LinkService;
import com.example.linkscope.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
@Tag(name = "Links", description = "CRUD and analytics operations for short links")
public class LinkController {

    private final LinkService linkService;
    private final StatsService statsService;

    @PostMapping
    @Operation(summary = "Create short link", description = "Creates a short link with optional custom alias and expiration")
    @ApiResponse(responseCode = "201", description = "Link created")
    @ApiResponse(responseCode = "409", description = "Alias conflict", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<LinkResponse> createLink(@Valid @RequestBody CreateLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(linkService.createLink(request));
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Get link info", description = "Returns link information without redirect")
    @ApiResponse(responseCode = "200", description = "Link found")
    @ApiResponse(responseCode = "404", description = "Link not found", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<LinkResponse> getLink(
            @Parameter(description = "Short code", required = true)
            @PathVariable String shortCode
    ) {
        return ResponseEntity.ok(linkService.getLinkByShortCode(shortCode));
    }

    @GetMapping
    @Operation(summary = "Get links list", description = "Returns paginated list of links")
    public ResponseEntity<Page<LinkSummaryResponse>> getLinks(
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(linkService.getLinks(pageable));
    }

    @PatchMapping("/{shortCode}")
    @Operation(summary = "Update link", description = "Updates original URL, expiration date, or active status")
    @ApiResponse(responseCode = "200", description = "Link updated")
    @ApiResponse(responseCode = "404", description = "Link not found", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<LinkResponse> updateLink(
            @PathVariable String shortCode,
            @Valid @RequestBody UpdateLinkRequest request
    ) {
        return ResponseEntity.ok(linkService.updateLink(shortCode, request));
    }

    @DeleteMapping("/{shortCode}")
    @Operation(summary = "Delete link", description = "Soft-deletes link by setting active=false")
    @ApiResponse(responseCode = "204", description = "Link deleted")
    public ResponseEntity<Void> deleteLink(@PathVariable String shortCode) {
        linkService.softDeleteLink(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shortCode}/stats")
    @Operation(summary = "Get link stats", description = "Returns aggregated click statistics and recent events")
    @ApiResponse(responseCode = "200", description = "Stats found")
    @ApiResponse(responseCode = "404", description = "Link not found", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<LinkStatsResponse> getStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(statsService.getStatsByShortCode(shortCode));
    }
}
