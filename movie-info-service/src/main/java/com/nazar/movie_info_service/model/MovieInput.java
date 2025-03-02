package com.nazar.movie_info_service.model;

public class MovieInput {

    private String id;
    private String name;
    private String description;

    public MovieInput(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public MovieInput() {
    }

    public MovieInput(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
