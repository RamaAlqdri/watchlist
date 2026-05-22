package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieRequest;
import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.entity.User;
import com.example.moviewatchlist.enums.MovieGenre;
import com.example.moviewatchlist.repository.MovieRepository;
import com.example.moviewatchlist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

abstract class MovieServiceTestSupport {

    protected static final String USERNAME = "budi";

    @Mock
    protected MovieRepository movieRepository;

    @Mock
    protected UserRepository userRepository;

    protected MovieService movieService;
    protected User user;
    protected Movie movie;

    @BeforeEach
    void setUpBase() {
        movieService = new MovieService(movieRepository, userRepository);
        user = createUser();
        movie = createMovie(user);
        setAuthenticatedUser(USERNAME);
    }

    @AfterEach
    void tearDownBase() {
        SecurityContextHolder.clearContext();
    }

    protected User createUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("Budi");
        newUser.setUsername(USERNAME);
        newUser.setPassword("hashed-password");
        return newUser;
    }

    protected Movie createMovie(User owner) {
        Movie newMovie = new Movie();
        newMovie.setId(1L);
        newMovie.setTitle("Interstellar");
        newMovie.setGenre(MovieGenre.SCI_FI);
        newMovie.setDescription("Space movie");
        newMovie.setWatched(false);
        newMovie.setRating(5);
        newMovie.setPosterUrl("https://example.com/interstellar.jpg");
        newMovie.setCreatedAt(LocalDateTime.now());
        newMovie.setUser(owner);
        return newMovie;
    }

    protected MovieRequest createMovieRequest() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setGenre(MovieGenre.ACTION);
        request.setDescription("Dream heist movie");
        request.setWatched(true);
        request.setRating(4);
        request.setPosterUrl("https://example.com/inception.jpg");
        return request;
    }

    protected void mockCurrentUserFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    }

    protected void mockCurrentUserNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
    }

    protected void mockMovieFound(Long movieId) {
        when(movieRepository.findByIdAndUser(movieId, user)).thenReturn(Optional.of(movie));
    }

    protected void mockMovieNotFound(Long movieId) {
        when(movieRepository.findByIdAndUser(movieId, user)).thenReturn(Optional.empty());
    }

    protected void setAuthenticatedUser(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }

    protected void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
