package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieRequest;
import com.example.moviewatchlist.dto.MovieResponse;
import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.enums.MovieGenre;
import com.example.moviewatchlist.exception.ForbiddenException;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMovieServiceTest extends MovieServiceTestSupport {

    @Test
    void pathP1_whenAuthenticationMissing_shouldThrowForbiddenException() {
        MovieRequest request = createMovieRequest();
        clearAuthentication();

        assertThrows(ForbiddenException.class, () -> movieService.createMovie(request));

        verifyNoInteractions(userRepository, movieRepository);
    }

    @Test
    void pathP2_whenAuthenticatedUserNotFound_shouldThrowResourceNotFoundException() {
        MovieRequest request = createMovieRequest();
        mockCurrentUserNotFound();

        assertThrows(ResourceNotFoundException.class, () -> movieService.createMovie(request));

        verify(userRepository).findByUsername(USERNAME);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void pathP3_whenDescriptionAndPosterUrlBlank_shouldSaveNullOptionalFields() {
        MovieRequest request = createMovieRequest();
        request.setDescription(" ");
        request.setPosterUrl("");
        mockCurrentUserFound();
        mockSaveMovie();

        MovieResponse response = movieService.createMovie(request);

        Movie savedMovie = captureSavedMovie();
        assertEquals(2L, response.getId());
        assertNull(savedMovie.getDescription());
        assertNull(savedMovie.getPosterUrl());
        assertSame(user, savedMovie.getUser());
    }

    @Test
    void pathP4_whenDescriptionFilledAndPosterUrlBlank_shouldTrimDescriptionAndSaveNullPosterUrl() {
        MovieRequest request = createMovieRequest();
        request.setDescription("  Dream heist movie  ");
        request.setPosterUrl(" ");
        mockCurrentUserFound();
        mockSaveMovie();

        MovieResponse response = movieService.createMovie(request);

        Movie savedMovie = captureSavedMovie();
        assertEquals(2L, response.getId());
        assertEquals("Dream heist movie", savedMovie.getDescription());
        assertNull(savedMovie.getPosterUrl());
        assertSame(user, savedMovie.getUser());
    }

    @Test
    void pathP5_whenDescriptionAndPosterUrlFilled_shouldSaveCompleteMovie() {
        MovieRequest request = createMovieRequest();
        request.setDescription("  Dream heist movie  ");
        request.setPosterUrl("  https://example.com/inception.jpg  ");
        mockCurrentUserFound();
        mockSaveMovie();

        MovieResponse response = movieService.createMovie(request);

        Movie savedMovie = captureSavedMovie();
        assertEquals(2L, response.getId());
        assertEquals("Inception", savedMovie.getTitle());
        assertEquals(MovieGenre.ACTION, savedMovie.getGenre());
        assertEquals("Dream heist movie", savedMovie.getDescription());
        assertEquals("https://example.com/inception.jpg", savedMovie.getPosterUrl());
        assertEquals(4, savedMovie.getRating());
        assertSame(user, savedMovie.getUser());
    }

    private void mockSaveMovie() {
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie savedMovie = invocation.getArgument(0);
            savedMovie.setId(2L);
            savedMovie.setCreatedAt(LocalDateTime.now());
            return savedMovie;
        });
    }

    private Movie captureSavedMovie() {
        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(movieCaptor.capture());
        return movieCaptor.getValue();
    }
}
