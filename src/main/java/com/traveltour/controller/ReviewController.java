package com.traveltour.controller;

import com.traveltour.dto.ReviewDto;
import com.traveltour.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/destinations/{destinationId}/reviews")
    public ResponseEntity<List<ReviewDto.Response>> getByDestination(@PathVariable Long destinationId) {
        return ResponseEntity.ok(reviewService.findByDestination(destinationId));
    }

    @PostMapping("/api/destinations/{destinationId}/reviews")
    public ResponseEntity<ReviewDto.Response> create(@PathVariable Long destinationId,
                                                      @Valid @RequestBody ReviewDto.Request request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reviewService.create(destinationId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/api/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
