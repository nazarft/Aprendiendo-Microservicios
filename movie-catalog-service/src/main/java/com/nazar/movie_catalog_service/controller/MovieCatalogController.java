package com.nazar.movie_catalog_service.controller;

import com.nazar.movie_catalog_service.model.CatalogItem;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        return List.of(
                new CatalogItem("Transformers", "Test", 4),
                new CatalogItem("Avengers", "Test", 5)
        );
    }

}
