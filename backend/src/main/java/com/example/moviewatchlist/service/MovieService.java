package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.MovieRequest;
import com.example.moviewatchlist.dto.MovieResponse;
import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.entity.User;
import com.example.moviewatchlist.exception.ForbiddenException;
import com.example.moviewatchlist.exception.ResourceNotFoundException;
import com.example.moviewatchlist.repository.MovieRepository;
import com.example.moviewatchlist.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public MovieService(MovieRepository movieRepository, UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getMovies(String search) {
        User currentUser = getCurrentUser();
        List<Movie> movies;

        if (search == null || search.isBlank()) {
            movies = movieRepository.findByUserOrderByCreatedAtDesc(currentUser);
        } else {
            movies = movieRepository.findByUserAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                    currentUser,
                    search.trim()
            );
        }

        return movies.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long id) {
        Movie movie = findOwnedMovie(id);
        return toResponse(movie);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        User currentUser = getCurrentUser();

        Movie movie = new Movie();
        applyRequest(movie, request);
        movie.setUser(currentUser);

        return toResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = findOwnedMovie(id);
        applyRequest(movie, request);

        return toResponse(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = findOwnedMovie(id);
        movieRepository.delete(movie);
    }

    @Transactional
    public MovieResponse toggleWatched(Long id) {
        Movie movie = findOwnedMovie(id);
        movie.setWatched(!movie.isWatched());

        return toResponse(movieRepository.save(movie));
    }

    private Movie findOwnedMovie(Long id) {
        User currentUser = getCurrentUser();
        return movieRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Movie tidak ditemukan"));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("User belum login");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private void applyRequest(Movie movie, MovieRequest request) {
        movie.setTitle(request.getTitle().trim());
        movie.setGenre(request.getGenre());
        movie.setDescription(normalizeOptionalText(request.getDescription()));
        movie.setWatched(request.isWatched());
        movie.setRating(request.getRating());
        movie.setPosterUrl(normalizeOptionalText(request.getPosterUrl()));
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDescription(),
                movie.isWatched(),
                movie.getRating(),
                movie.getPosterUrl(),
                movie.getCreatedAt()
        );
    }
}
