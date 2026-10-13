package com.traveltour.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

public class ReviewDto {

    @Data
    public static class Request {
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;

        private String comment;
    }

    @Data
    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private Long destinationId;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}
