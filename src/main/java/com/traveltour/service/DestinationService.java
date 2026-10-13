package com.traveltour.service;

import com.traveltour.dto.DestinationDto;
import com.traveltour.entity.Destination;
import com.traveltour.exception.ResourceNotFoundException;
import com.traveltour.repository.DestinationRepository;
import com.traveltour.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final ReviewRepository reviewRepository;

    public List<DestinationDto.Response> findAll() {
        return destinationRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public DestinationDto.Response findById(Long id) {
        Destination dest = destinationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));
        return toResponse(dest);
    }

    @Transactional
    public DestinationDto.Response create(DestinationDto.Request request) {
        Destination destination = Destination.builder()
            .name(request.getName())
            .country(request.getCountry())
            .description(request.getDescription())
            .imageUrl(request.getImageUrl())
            .build();
        return toResponse(destinationRepository.save(destination));
    }

    @Transactional
    public DestinationDto.Response update(Long id, DestinationDto.Request request) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));

        destination.setName(request.getName());
        destination.setCountry(request.getCountry());
        destination.setDescription(request.getDescription());
        destination.setImageUrl(request.getImageUrl());

        return toResponse(destinationRepository.save(destination));
    }

    @Transactional
    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Destination not found with id: " + id);
        }
        destinationRepository.deleteById(id);
    }

    private DestinationDto.Response toResponse(Destination dest) {
        DestinationDto.Response response = new DestinationDto.Response();
        response.setId(dest.getId());
        response.setName(dest.getName());
        response.setCountry(dest.getCountry());
        response.setDescription(dest.getDescription());
        response.setImageUrl(dest.getImageUrl());
        response.setCreatedAt(dest.getCreatedAt());

        Double avg = reviewRepository.findAverageRatingByDestinationId(dest.getId());
        response.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null);

        List<?> reviews = dest.getReviews();
        response.setReviewCount(reviews != null ? reviews.size() : 0);

        return response;
    }
}
