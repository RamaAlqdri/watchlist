package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieRequest;
import com.example.moviewatchlist.dto.MovieResponse;
import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.entity.User;
import com.example.moviewatchlist.enums.MovieGenre;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import com.example.moviewatchlist.repository.MovieRepository;
import com.example.moviewatchlist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MovieService movieService;

    private User user;
    private Movie movie;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Budi");
        user.setUsername("budi");
        user.setPassword("hashed-password");

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Interstellar");
        movie.setGenre(MovieGenre.SCI_FI);
        movie.setDescription("Space movie");
        movie.setWatched(false);
        movie.setRating(5);
        movie.setPosterUrl("https://example.com/interstellar.jpg");
        movie.setCreatedAt(LocalDateTime.now());
        movie.setUser(user);

        setAuthenticatedUser("budi");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMovie_whenMovieBelongsToUser_shouldReturnMovieResponse() {
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(movie));

        MovieResponse response = movieService.getMovie(1L);

        assertEquals(1L, response.getId());
        assertEquals("Interstellar", response.getTitle());
        assertEquals(MovieGenre.SCI_FI, response.getGenre());
        assertEquals(5, response.getRating());
        verify(movieRepository).findByIdAndUser(1L, user);
    }

    @Test
    void getMovie_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovie(99L));

        verify(movieRepository).findByIdAndUser(99L, user);
    }

    @Test
    void createMovie_whenRequestValid_shouldSaveMovieForCurrentUser() {
        MovieRequest request = createMovieRequest();

        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie savedMovie = invocation.getArgument(0);
            savedMovie.setId(2L);
            savedMovie.setCreatedAt(LocalDateTime.now());
            return savedMovie;
        });

        MovieResponse response = movieService.createMovie(request);

        assertEquals(2L, response.getId());
        assertEquals("Inception", response.getTitle());
        assertEquals(MovieGenre.ACTION, response.getGenre());
        assertEquals(4, response.getRating());

        verify(movieRepository).save(argThat(savedMovie ->
                savedMovie.getUser().equals(user)
                        && savedMovie.getTitle().equals("Inception")
                        && savedMovie.getGenre() == MovieGenre.ACTION
        ));
    }

    @Test
    void createMovie_whenOptionalFieldsBlank_shouldSaveNullValues() {
        MovieRequest request = createMovieRequest();
        request.setDescription("   ");
        request.setPosterUrl("");

        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie savedMovie = invocation.getArgument(0);
            savedMovie.setId(2L);
            savedMovie.setCreatedAt(LocalDateTime.now());
            return savedMovie;
        });

        movieService.createMovie(request);

        verify(movieRepository).save(argThat(savedMovie ->
                savedMovie.getDescription() == null
                        && savedMovie.getPosterUrl() == null
        ));
    }

    @Test
    void updateMovie_whenMovieBelongsToUser_shouldUpdateMovieData() {
        MovieRequest request = createMovieRequest();

        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.updateMovie(1L, request);

        assertEquals("Inception", response.getTitle());
        assertEquals(MovieGenre.ACTION, response.getGenre());
        assertEquals(4, response.getRating());
        assertTrue(response.isWatched());

        verify(movieRepository).findByIdAndUser(1L, user);
        verify(movieRepository).save(movie);
    }

    @Test
    void updateMovie_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        MovieRequest request = createMovieRequest();

        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(99L, request));

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovie_whenMovieBelongsToUser_shouldDeleteMovie() {
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        verify(movieRepository).delete(movie);
    }

    @Test
    void deleteMovie_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(99L));

        verify(movieRepository, never()).delete(any(Movie.class));
    }

    @Test
    void toggleWatched_whenInitiallyFalse_shouldChangeToTrue() {
        movie.setWatched(false);

        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.toggleWatched(1L);

        assertTrue(response.isWatched());
        verify(movieRepository).save(movie);
    }

    @Test
    void toggleWatched_whenInitiallyTrue_shouldChangeToFalse() {
        movie.setWatched(true);

        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponse response = movieService.toggleWatched(1L);

        assertFalse(response.isWatched());
        verify(movieRepository).save(movie);
    }

    @Test
    void toggleWatched_whenMovieNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findByUsername("budi")).thenReturn(Optional.of(user));
        when(movieRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.toggleWatched(99L));

        verify(movieRepository, never()).save(any(Movie.class));
    }

    private MovieRequest createMovieRequest() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setGenre(MovieGenre.ACTION);
        request.setDescription("Dream heist movie");
        request.setWatched(true);
        request.setRating(4);
        request.setPosterUrl("https://example.com/inception.jpg");
        return request;
    }

    private void setAuthenticatedUser(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }
}
