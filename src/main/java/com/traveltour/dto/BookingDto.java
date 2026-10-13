package com.traveltour.dto;

import com.traveltour.entity.Booking;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDto {

    @Data
    public static class Request {
        @NotNull(message = "Package ID is required")
        private Long packageId;

        @NotNull(message = "Travel date is required")
        @Future(message = "Travel date must be in the future")
        private LocalDate travelDate;

        @NotNull(message = "Number of persons is required")
        @Min(value = 1, message = "At least 1 person required")
        private Integer numPersons;
    }

    @Data
    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private Long packageId;
        private String packageTitle;
        private String destinationName;
        private LocalDate travelDate;
        private Integer numPersons;
        private BigDecimal totalPrice;
        private Booking.BookingStatus status;
        private LocalDateTime createdAt;
    }
}
