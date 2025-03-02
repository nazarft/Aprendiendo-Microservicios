package com.nazar.movie_catalog_service.controller.graphql;


import com.nazar.movie_catalog_service.model.Movie;

public class MovieGraphQLResponse {
    private Data data;
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Movie movieById;
        public Movie getMovieById() {
            return movieById;
        }
        public void setMovieById(Movie movieById) {
            this.movieById = movieById;
        }
    }

}
