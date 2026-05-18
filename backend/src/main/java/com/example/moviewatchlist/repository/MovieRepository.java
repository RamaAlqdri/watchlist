package com.example.moviewatchlist.repository;

import com.example.moviewatchlist.entity.Movie;
import com.example.moviewatchlist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByUserOrderByCreatedAtDesc(User user);

    List<Movie> findByUserAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(User user, String title);

    Optional<Movie> findByIdAndUser(Long id, User user);
}
