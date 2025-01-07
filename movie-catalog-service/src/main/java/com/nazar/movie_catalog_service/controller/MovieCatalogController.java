package com.nazar.movie_catalog_service.controller;

import com.nazar.movie_catalog_service.model.CatalogItem;
import com.nazar.movie_catalog_service.model.Movie;
import com.nazar.movie_catalog_service.model.UserRating;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @RequestMapping("/{userId}")
    @CircuitBreaker(name = "movie-catalog-service", fallbackMethod = "fallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) throws Exception {
            UserRating userRatings = webClientBuilder.build()
                    .get()
                    .uri("http://ratings-data-service/ratingsdata/user/" + userId)
                    .retrieve()
                    .bodyToMono(UserRating.class)
                    .block();
            return userRatings
                    .getRatings()
                    .stream()
                    .map(rating -> {
                        Movie movie = webClientBuilder.build()
                                        .get()
                                        .uri("http://movie-info-service/movies/" + rating.getMovieId())
                                        .retrieve()
                                        .bodyToMono(Movie.class)
                                        .block();

                        return new CatalogItem(
                                movie.getName(),
                                movie.getDescription(),
                                rating.getRating()
                        );
                    }).toList();
    }

    private List<CatalogItem> fallbackCatalog(Exception e) {
        return List.of(new CatalogItem("Película no disponible", "", 0));
    }
}
