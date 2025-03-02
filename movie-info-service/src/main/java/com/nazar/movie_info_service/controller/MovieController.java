package com.nazar.movie_info_service.controller;

import com.nazar.movie_info_service.model.Movie;
import com.nazar.movie_info_service.model.MovieInput;
import com.nazar.movie_info_service.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class MovieController {

    private final MovieRepository movieRepository;
    @Autowired
    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @QueryMapping(name = "movieById")
    public Movie getMovieInfo(@Argument String movieId) {
        return movieRepository.findById(movieId).orElse(new Movie(movieId, "Película desconocida", "Sin descripción"));
    }
    @MutationMapping
    public Movie addMovie(@Argument MovieInput movieInput){
        Movie movie = new Movie();
        movie.setMovieId(movieInput.getId());
        movie.setName(movieInput.getName());
        movie.setDescription(movieInput.getDescription());
        return movieRepository.save(movie);
    }

}
