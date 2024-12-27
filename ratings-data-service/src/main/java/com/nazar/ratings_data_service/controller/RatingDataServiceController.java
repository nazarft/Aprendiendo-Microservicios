package com.nazar.ratings_data_service.controller;

import com.nazar.ratings_data_service.model.Rating;
import com.nazar.ratings_data_service.model.UserRating;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ratingsdata")
public class RatingDataServiceController {

    private final Map<String, List<Rating>> userRatings = new HashMap<>();
    public RatingDataServiceController() {
        userRatings.put("user1", List.of(
                new Rating("MOV101", 5),
                new Rating("MOV102", 4)
        ));
        userRatings.put("user2", List.of(
                new Rating("MOV201", 3),
                new Rating("MOV202", 2)
        ));
    }
    // Obtener calificación para una película específica
    @RequestMapping("/{movieId}")
    public Rating getRating(@PathVariable("movieId") String movieId) {
        return new Rating(movieId, 4);
    }

    // Obtener calificaciones de un usuario específico
    @RequestMapping("/user/{userId}")
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        List<Rating> ratings = userRatings.getOrDefault(userId, List.of(
                new Rating("MOV999", 1) // Calificación por defecto si el usuario no existe
        ));
        UserRating userRating = new UserRating();
        userRating.setUserRatings(ratings);
        return userRating;
    }
}
