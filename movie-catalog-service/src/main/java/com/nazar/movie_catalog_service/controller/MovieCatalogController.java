package com.nazar.movie_catalog_service.controller;

import com.nazar.movie_catalog_service.model.CatalogItem;
import com.nazar.movie_catalog_service.model.Movie;
import com.nazar.movie_catalog_service.model.Rating;
import com.nazar.movie_catalog_service.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogController(RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId,
                UserRating.class);

        /*List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );*/
        return ratings
                .getUserRatings()
                .stream()
                .map(rating -> {
                   // For each movie ID, call movie info service and get details
                    Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(),
                            Movie.class);
                    // Put them all together
                    return new CatalogItem(movie.getName(), "Desc", rating.getRating());

                   /* Movie movie = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8081/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();
                    return new CatalogItem(movie.getName(), "Desc", rating.getRating());*/

        }).toList();
    }

}
