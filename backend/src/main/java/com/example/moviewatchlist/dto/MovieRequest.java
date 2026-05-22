package com.example.moviewatchlist.dto;

import com.example.moviewatchlist.enums.MovieGenre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRequest {

    @NotBlank(message = "Title wajib diisi")
    @Size(max = 150, message = "Title maksimal 150 karakter")
    private String title;

    @NotNull(message = "Genre wajib dipilih")
    private MovieGenre genre;

    private String description;

    private boolean watched;

    @NotNull(message = "Rating wajib diisi")
    @Min(value = 1, message = "Rating minimal 1")
    @Max(value = 5, message = "Rating maksimal 5")
    private Integer rating;

    @Size(max = 500, message = "Poster URL maksimal 500 karakter")
    private String posterUrl;
}
