package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieRequest;
import com.example.moviewatchlist.dto.MovieResponse;
import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.enums.MovieGenre;
import com.example.moviewatchlist.exception.ForbiddenException;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateMovieServiceTest extends MovieServiceTestSupport {

    @Test
    void pathP1_whenAuthenticationMissing_shouldThrowForbiddenException() {
        MovieRequest request = createMovieRequest();
        clearAuthentication();

        assertThrows(ForbiddenException.class, () -> movieService.updateMovie(1L, request));

        verifyNoInteractions(userRepository, movieRepository);
    }

    @Test
    void pathP2_whenAuthenticatedUserNotFound_shouldThrowResourceNotFoundException() {
        MovieRequest request = createMovieRequest();
        mockCurrentUserNotFound();

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(1L, request));

        verify(userRepository).findByUsername(USERNAME);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void pathP3_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        MovieRequest request = createMovieRequest();
        mockCurrentUserFound();
        mockMovieNotFound(99L);

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(99L, request));

        verify(movieRepository).findByIdAndUser(99L, user);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void pathP4_whenDescriptionAndPosterUrlBlank_shouldUpdateOptionalFieldsToNull() {
        MovieRequest request = createMovieRequest();
        request.setDescription("");
        request.setPosterUrl(" ");
        mockCurrentUserFound();
        mockMovieFound(1L);
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.updateMovie(1L, request);

        assertEquals("Inception", response.getTitle());
        assertNull(movie.getDescription());
        assertNull(movie.getPosterUrl());
        assertTrue(response.isWatched());
        verify(movieRepository).save(movie);
    }

    @Test
    void pathP5_whenDescriptionFilledAndPosterUrlBlank_shouldTrimDescriptionAndSaveNullPosterUrl() {
        MovieRequest request = createMovieRequest();
        request.setDescription("  Updated description  ");
        request.setPosterUrl("");
        mockCurrentUserFound();
        mockMovieFound(1L);
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.updateMovie(1L, request);

        assertEquals("Inception", response.getTitle());
        assertEquals("Updated description", movie.getDescription());
        assertNull(movie.getPosterUrl());
        assertEquals(MovieGenre.ACTION, response.getGenre());
        verify(movieRepository).save(movie);
    }

    @Test
    void pathP6_whenDescriptionAndPosterUrlFilled_shouldUpdateCompleteMovie() {
        MovieRequest request = createMovieRequest();
        request.setDescription("  Updated description  ");
        request.setPosterUrl("  https://example.com/updated.jpg  ");
        mockCurrentUserFound();
        mockMovieFound(1L);
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.updateMovie(1L, request);

        assertEquals("Inception", response.getTitle());
        assertEquals(MovieGenre.ACTION, response.getGenre());
        assertEquals("Updated description", movie.getDescription());
        assertEquals("https://example.com/updated.jpg", movie.getPosterUrl());
        assertEquals(4, response.getRating());
        assertTrue(response.isWatched());
        verify(movieRepository).save(movie);
    }
}
