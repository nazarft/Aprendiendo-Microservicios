package com.nazar.movie_info_service.controller;

import com.nazar.movie_info_service.model.Movie;
import com.nazar.movie_info_service.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    @Autowired
    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    
   /* private final Map<String, Movie> movieData = new HashMap<>();
    public MovieController() {
        movieData.put("MOV101", new Movie("MOV101", "El Señor de los Anillos", "Un anillo para gobernarlos a todos"));
        movieData.put("MOV102", new Movie("MOV102", "Interestelar", "Un grupo de astronautas busca un nuevo hogar para la humanidad"));
        movieData.put("MOV201", new Movie("MOV201", "El Padrino", "La historia de la familia Corleone"));
        movieData.put("MOV202", new Movie("MOV202", "Matrix", "La realidad no es lo que parece"));
    }*/
    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
        return movieRepository.findById(movieId).orElse(new Movie(movieId, "Película desconocida", "Sin descripción"));
    }
}
