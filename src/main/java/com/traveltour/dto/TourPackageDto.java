package com.traveltour.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TourPackageDto {

    @Data
    public static class Request {
        @NotNull(message = "Destination ID is required")
        private Long destinationId;

        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be positive")
        private BigDecimal price;

        @NotNull(message = "Duration days is required")
        @Min(value = 1, message = "Duration must be at least 1 day")
        private Integer durationDays;

        @NotNull(message = "Max capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        private Integer maxCapacity;

        private String imageUrl;
    }

    @Data
    public static class Response {
        private Long id;
        private Long destinationId;
        private String destinationName;
        private String destinationCountry;
        private String title;
        private String description;
        private BigDecimal price;
        private Integer durationDays;
        private Integer maxCapacity;
        private String imageUrl;
        private LocalDateTime createdAt;
    }
}
