package com.traveltour.service;

import com.traveltour.dto.ReviewDto;
import com.traveltour.entity.Destination;
import com.traveltour.entity.Review;
import com.traveltour.entity.User;
import com.traveltour.exception.BadRequestException;
import com.traveltour.exception.ResourceNotFoundException;
import com.traveltour.repository.DestinationRepository;
import com.traveltour.repository.ReviewRepository;
import com.traveltour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;

    public List<ReviewDto.Response> findByDestination(Long destinationId) {
        return reviewRepository.findByDestinationId(destinationId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public ReviewDto.Response create(Long destinationId, ReviewDto.Request request, String userEmail) {
        Destination destination = destinationRepository.findById(destinationId)
            .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + destinationId));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = Review.builder()
            .user(user)
            .destination(destination)
            .rating(request.getRating())
            .comment(request.getComment())
            .build();

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void delete(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        boolean isOwner = review.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.deleteById(reviewId);
    }

    private ReviewDto.Response toResponse(Review review) {
        ReviewDto.Response response = new ReviewDto.Response();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUserName(review.getUser().getName());
        response.setDestinationId(review.getDestination().getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
