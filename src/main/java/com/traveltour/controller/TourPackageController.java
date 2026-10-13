package com.traveltour.controller;

import com.traveltour.dto.TourPackageDto;
import com.traveltour.service.TourPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class TourPackageController {

    private final TourPackageService tourPackageService;

    @GetMapping
    public ResponseEntity<List<TourPackageDto.Response>> getAll(
            @RequestParam(required = false) Long destinationId) {
        if (destinationId != null) {
            return ResponseEntity.ok(tourPackageService.findByDestination(destinationId));
        }
        return ResponseEntity.ok(tourPackageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackageDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tourPackageService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourPackageDto.Response> create(@Valid @RequestBody TourPackageDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourPackageService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourPackageDto.Response> update(@PathVariable Long id,
                                                           @Valid @RequestBody TourPackageDto.Request request) {
        return ResponseEntity.ok(tourPackageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourPackageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
