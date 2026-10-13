package com.traveltour.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DestinationDto {

    @Data
    public static class Request {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Country is required")
        private String country;

        private String description;
        private String imageUrl;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String country;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
        private Double averageRating;
        private Integer reviewCount;
    }
}
