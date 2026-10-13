package com.traveltour.repository;

import com.traveltour.entity.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TourPackageRepository extends JpaRepository<TourPackage, Long> {
    List<TourPackage> findByDestinationId(Long destinationId);
    List<TourPackage> findByTitleContainingIgnoreCase(String title);
}
