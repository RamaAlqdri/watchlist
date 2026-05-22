package com.example.moviewatchlist.dto;

import com.example.moviewatchlist.enums.MovieGenre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private MovieGenre genre;
    private String description;
    private boolean watched;
    private Integer rating;
    private String posterUrl;
    private LocalDateTime createdAt;
}
