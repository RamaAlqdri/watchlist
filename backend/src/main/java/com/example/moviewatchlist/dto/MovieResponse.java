package com.example.moviewatchlist.dto;

import com.example.moviewatchlist.enums.MovieGenre;

import java.time.LocalDateTime;

public class MovieResponse {

    private Long id;
    private String title;
    private MovieGenre genre;
    private String description;
    private boolean watched;
    private Integer rating;
    private String posterUrl;
    private LocalDateTime createdAt;

    public MovieResponse(Long id, String title, MovieGenre genre, String description, boolean watched,
                         Integer rating, String posterUrl, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.watched = watched;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
