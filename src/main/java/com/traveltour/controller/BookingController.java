package com.traveltour.controller;

import com.traveltour.dto.BookingDto;
import com.traveltour.repository.UserRepository;
import com.traveltour.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    // Admin: get all bookings
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDto.Response>> getAll() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    // User: get own bookings
    @GetMapping("/my")
    public ResponseEntity<List<BookingDto.Response>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow().getId();
        return ResponseEntity.ok(bookingService.findByUser(userId));
    }

    // User: create booking
    @PostMapping
    public ResponseEntity<BookingDto.Response> create(@Valid @RequestBody BookingDto.Request request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookingService.create(request, userDetails.getUsername()));
    }

    // User/Admin: cancel booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDto.Response> cancel(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.cancel(id, userDetails.getUsername()));
    }
}
