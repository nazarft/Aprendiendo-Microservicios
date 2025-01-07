package com.nazar.ratings_data_service.repository;

import com.nazar.ratings_data_service.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingsDataRepository extends JpaRepository<Rating, String> {
    List<Rating> findByUserId(String userId);
}
