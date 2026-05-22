package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieResponse;
import com.example.moviewatchlist.enums.MovieGenre;
import com.example.moviewatchlist.exception.ForbiddenException;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class GetMovieServiceTest extends MovieServiceTestSupport {

    @Test
    void pathP1_whenAuthenticationMissing_shouldThrowForbiddenException() {
        clearAuthentication();

        assertThrows(ForbiddenException.class, () -> movieService.getMovie(1L));

        verifyNoInteractions(userRepository, movieRepository);
    }

    @Test
    void pathP2_whenAuthenticatedUserNotFound_shouldThrowResourceNotFoundException() {
        mockCurrentUserNotFound();

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovie(1L));

        verify(userRepository).findByUsername(USERNAME);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void pathP3_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        mockCurrentUserFound();
        mockMovieNotFound(99L);

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovie(99L));

        verify(movieRepository).findByIdAndUser(99L, user);
    }

    @Test
    void pathP4_whenMovieBelongsToUser_shouldReturnMovieResponse() {
        mockCurrentUserFound();
        mockMovieFound(1L);

        MovieResponse response = movieService.getMovie(1L);

        assertEquals(1L, response.getId());
        assertEquals("Interstellar", response.getTitle());
        assertEquals(MovieGenre.SCI_FI, response.getGenre());
        assertEquals(5, response.getRating());
        verify(movieRepository).findByIdAndUser(1L, user);
    }
}
