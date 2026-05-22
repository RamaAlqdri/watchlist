package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieResponse;
import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.exception.ForbiddenException;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleWatchedServiceTest extends MovieServiceTestSupport {

    @Test
    void pathP1_whenAuthenticationMissing_shouldThrowForbiddenException() {
        clearAuthentication();

        assertThrows(ForbiddenException.class, () -> movieService.toggleWatched(1L));

        verifyNoInteractions(userRepository, movieRepository);
    }

    @Test
    void pathP2_whenAuthenticatedUserNotFound_shouldThrowResourceNotFoundException() {
        mockCurrentUserNotFound();

        assertThrows(ResourceNotFoundException.class, () -> movieService.toggleWatched(1L));

        verify(userRepository).findByUsername(USERNAME);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void pathP3_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        mockCurrentUserFound();
        mockMovieNotFound(99L);

        assertThrows(ResourceNotFoundException.class, () -> movieService.toggleWatched(99L));

        verify(movieRepository).findByIdAndUser(99L, user);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void pathP4_whenMovieInitiallyUnwatched_shouldChangeToWatched() {
        movie.setWatched(false);
        mockCurrentUserFound();
        mockMovieFound(1L);
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.toggleWatched(1L);

        assertTrue(response.isWatched());
        verify(movieRepository).save(movie);
    }

    @Test
    void pathP4_whenMovieInitiallyWatched_shouldChangeToUnwatched() {
        movie.setWatched(true);
        mockCurrentUserFound();
        mockMovieFound(1L);
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.toggleWatched(1L);

        assertFalse(response.isWatched());
        verify(movieRepository).save(movie);
    }
}
