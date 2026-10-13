package com.traveltour.service;

import com.traveltour.dto.BookingDto;
import com.traveltour.entity.Booking;
import com.traveltour.entity.TourPackage;
import com.traveltour.entity.User;
import com.traveltour.exception.BadRequestException;
import com.traveltour.exception.ResourceNotFoundException;
import com.traveltour.repository.BookingRepository;
import com.traveltour.repository.TourPackageRepository;
import com.traveltour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TourPackageRepository tourPackageRepository;
    private final UserRepository userRepository;

    public List<BookingDto.Response> findAll() {
        return bookingRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<BookingDto.Response> findByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public BookingDto.Response create(BookingDto.Request request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TourPackage pkg = tourPackageRepository.findById(request.getPackageId())
            .orElseThrow(() -> new ResourceNotFoundException("Tour package not found with id: " + request.getPackageId()));

        // Check capacity
        long booked = bookingRepository.findByTourPackageId(pkg.getId()).stream()
            .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
            .mapToInt(Booking::getNumPersons)
            .sum();

        if (booked + request.getNumPersons() > pkg.getMaxCapacity()) {
            throw new BadRequestException("Not enough capacity. Available: " + (pkg.getMaxCapacity() - booked));
        }

        BigDecimal totalPrice = pkg.getPrice().multiply(BigDecimal.valueOf(request.getNumPersons()));

        Booking booking = Booking.builder()
            .user(user)
            .tourPackage(pkg)
            .travelDate(request.getTravelDate())
            .numPersons(request.getNumPersons())
            .totalPrice(totalPrice)
            .status(Booking.BookingStatus.CONFIRMED)
            .build();

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto.Response cancel(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    private BookingDto.Response toResponse(Booking booking) {
        BookingDto.Response response = new BookingDto.Response();
        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setUserName(booking.getUser().getName());
        response.setPackageId(booking.getTourPackage().getId());
        response.setPackageTitle(booking.getTourPackage().getTitle());
        response.setDestinationName(booking.getTourPackage().getDestination().getName());
        response.setTravelDate(booking.getTravelDate());
        response.setNumPersons(booking.getNumPersons());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
}
