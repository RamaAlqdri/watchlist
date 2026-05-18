# Movie Watchlist

Movie Watchlist adalah aplikasi fullstack sederhana untuk menyimpan daftar film pribadi.

Progress saat ini:

1. Inisialisasi struktur project backend dan frontend.
2. Konfigurasi Maven untuk Spring Boot 3.x, Java 21, Spring Security, JWT, Spring Data JPA, PostgreSQL, dan Bean Validation.
3. Main application class Spring Boot.
4. Konfigurasi PostgreSQL untuk database `movie_watchlist_db`.
5. Schema SQL PostgreSQL untuk tabel `users` dan `movies`.

Struktur awal:

```text
backend/
  pom.xml
  src/main/java/com/example/moviewatchlist/MovieWatchlistApplication.java
frontend/
  css/
  js/
```

## Setup PostgreSQL

Konfigurasi default backend menggunakan:

- Host: `localhost`
- Port: `5432`
- Database: `movie_watchlist_db`
- Username: `postgres`
- Password: `postgres`

Buat database:

```sql
CREATE DATABASE movie_watchlist_db;
```

Schema manual tersedia di:

```text
backend/src/main/resources/schema.sql
```

Backend juga memakai `spring.jpa.hibernate.ddl-auto=update`, sehingga tabel akan dibuat atau diperbarui otomatis oleh Hibernate saat aplikasi berjalan.
