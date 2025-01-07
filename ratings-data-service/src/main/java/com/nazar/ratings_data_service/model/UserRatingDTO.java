package com.nazar.ratings_data_service.model;

import java.util.List;

public class UserRatingDTO {
    private List<Rating> ratings;

    public UserRatingDTO() {
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
