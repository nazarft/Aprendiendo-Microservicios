package com.nazar.ratings_data_service.controller;

import com.nazar.ratings_data_service.model.Rating;
import com.nazar.ratings_data_service.model.RatingInput;
import com.nazar.ratings_data_service.model.UserRatingDTO;
import com.nazar.ratings_data_service.repository.RatingsDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratingsdata")
public class RatingDataServiceController {

    private final RatingsDataRepository ratingsDataRepository;

    @Autowired
    public RatingDataServiceController(RatingsDataRepository ratingsDataRepository) {
        this.ratingsDataRepository = ratingsDataRepository;
    }
    @RequestMapping("/user/{userId}")
    public UserRatingDTO getUserRating(@PathVariable("userId") String userId) {
        List<Rating> ratings = ratingsDataRepository.findByUserId(userId);
        UserRatingDTO userRatingDTO = new UserRatingDTO();
        userRatingDTO.setRatings(ratings);
        return userRatingDTO;
    }
    @PostMapping("/add/{userId}")
    public void addRating(@PathVariable("userId") String userId, @RequestBody RatingInput ratingInput) {
        Rating rating = new Rating();
        rating.setUserId(userId);
        rating.setMovieId(ratingInput.getMovieId());
        rating.setRating(ratingInput.getRating());
        ratingsDataRepository.save(rating);
    }
}
