package com.traveltour.service;

import com.traveltour.dto.TourPackageDto;
import com.traveltour.entity.Destination;
import com.traveltour.entity.TourPackage;
import com.traveltour.exception.ResourceNotFoundException;
import com.traveltour.repository.DestinationRepository;
import com.traveltour.repository.TourPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final DestinationRepository destinationRepository;

    public List<TourPackageDto.Response> findAll() {
        return tourPackageRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<TourPackageDto.Response> findByDestination(Long destinationId) {
        return tourPackageRepository.findByDestinationId(destinationId).stream()
            .map(this::toResponse)
            .toList();
    }

    public TourPackageDto.Response findById(Long id) {
        TourPackage pkg = tourPackageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tour package not found with id: " + id));
        return toResponse(pkg);
    }

    @Transactional
    public TourPackageDto.Response create(TourPackageDto.Request request) {
        Destination destination = destinationRepository.findById(request.getDestinationId())
            .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + request.getDestinationId()));

        TourPackage pkg = TourPackage.builder()
            .destination(destination)
            .title(request.getTitle())
            .description(request.getDescription())
            .price(request.getPrice())
            .durationDays(request.getDurationDays())
            .maxCapacity(request.getMaxCapacity())
            .imageUrl(request.getImageUrl())
            .build();

        return toResponse(tourPackageRepository.save(pkg));
    }

    @Transactional
    public TourPackageDto.Response update(Long id, TourPackageDto.Request request) {
        TourPackage pkg = tourPackageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tour package not found with id: " + id));

        Destination destination = destinationRepository.findById(request.getDestinationId())
            .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + request.getDestinationId()));

        pkg.setDestination(destination);
        pkg.setTitle(request.getTitle());
        pkg.setDescription(request.getDescription());
        pkg.setPrice(request.getPrice());
        pkg.setDurationDays(request.getDurationDays());
        pkg.setMaxCapacity(request.getMaxCapacity());
        pkg.setImageUrl(request.getImageUrl());

        return toResponse(tourPackageRepository.save(pkg));
    }

    @Transactional
    public void delete(Long id) {
        if (!tourPackageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tour package not found with id: " + id);
        }
        tourPackageRepository.deleteById(id);
    }

    private TourPackageDto.Response toResponse(TourPackage pkg) {
        TourPackageDto.Response response = new TourPackageDto.Response();
        response.setId(pkg.getId());
        response.setDestinationId(pkg.getDestination().getId());
        response.setDestinationName(pkg.getDestination().getName());
        response.setDestinationCountry(pkg.getDestination().getCountry());
        response.setTitle(pkg.getTitle());
        response.setDescription(pkg.getDescription());
        response.setPrice(pkg.getPrice());
        response.setDurationDays(pkg.getDurationDays());
        response.setMaxCapacity(pkg.getMaxCapacity());
        response.setImageUrl(pkg.getImageUrl());
        response.setCreatedAt(pkg.getCreatedAt());
        return response;
    }
}
