package com.example.moviewatchlist.service;

import com.example.moviewatchlist.exception.ForbiddenException;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteMovieServiceTest extends MovieServiceTestSupport {

    @Test
    void pathP1_whenAuthenticationMissing_shouldThrowForbiddenException() {
        clearAuthentication();

        assertThrows(ForbiddenException.class, () -> movieService.deleteMovie(1L));

        verifyNoInteractions(userRepository, movieRepository);
    }

    @Test
    void pathP2_whenAuthenticatedUserNotFound_shouldThrowResourceNotFoundException() {
        mockCurrentUserNotFound();

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(1L));

        verify(userRepository).findByUsername(USERNAME);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void pathP3_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        mockCurrentUserFound();
        mockMovieNotFound(99L);

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(99L));

        verify(movieRepository).findByIdAndUser(99L, user);
        verify(movieRepository, never()).delete(movie);
    }

    @Test
    void pathP4_whenMovieBelongsToUser_shouldDeleteMovie() {
        mockCurrentUserFound();
        mockMovieFound(1L);

        movieService.deleteMovie(1L);

        verify(movieRepository).delete(movie);
    }
}
