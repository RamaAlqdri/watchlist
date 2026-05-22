# MyWatchlist

MyWatchlist adalah aplikasi fullstack sederhana untuk mengelola daftar film pribadi. Backend menggunakan Java 21, Spring Boot 3.x, Spring Security, simple token authentication, Spring Data JPA, PostgreSQL, Maven, Bean Validation, BCrypt, dan Lombok. Frontend menggunakan HTML, CSS, Vanilla JavaScript, Fetch API, dan LocalStorage untuk menyimpan token login.

## Struktur Project

```text
backend/
  pom.xml
  src/main/java/com/example/moviewatchlist/
    MovieWatchlistApplication.java
    config/
    controller/
    dto/
    entity/
    enums/
    exception/
    repository/
    security/
    service/
  src/main/resources/
    application.properties
    schema.sql
frontend/
  login.html
  register.html
  index.html
  movie-form.html
  css/style.css
  js/api.js
  js/auth.js
  js/movies.js
  js/movie-form.js
```

## Progress dan Commit

```bash
git add .
git commit -m "feat: initialize fullstack movie watchlist project"

git add .
git commit -m "feat: configure postgresql database"

git add .
git commit -m "feat: add user movie entities and genre enum"

git add .
git commit -m "feat: add dto validation and exception handling"

git add .
git commit -m "feat: implement simple token authentication"

git add .
git commit -m "feat: implement movie crud api"

git add .
git commit -m "feat: add frontend authentication pages"

git add .
git commit -m "feat: add movie dashboard frontend"

git add .
git commit -m "feat: add movie form page"

git add .
git commit -m "docs: add setup guide and api examples"
```

## Database PostgreSQL

Konfigurasi default:

- Database: `movie_watchlist_db`
- Host: `localhost`
- Port: `5432`
- Username: `postgres`
- Password: `postgres`

Buat database:

```bash
createdb -U postgres movie_watchlist_db
```

Alternatif via `psql`:

```sql
CREATE DATABASE movie_watchlist_db;
```

Schema manual tersedia di `backend/src/main/resources/schema.sql`. Backend memakai `spring.jpa.hibernate.ddl-auto=update`, sehingga Hibernate dapat membuat atau memperbarui tabel saat aplikasi berjalan.

## Konfigurasi Backend

File: `backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/movie_watchlist_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Menjalankan Backend

Prasyarat:

- Java 21
- Maven 3.9+
- PostgreSQL aktif di port `5432`
- Database `movie_watchlist_db` sudah dibuat

Jalankan:

```bash
cd backend
mvn spring-boot:run
```

Backend berjalan di:

```text
http://localhost:8080
```

## Menjalankan Frontend

Jalankan static server di port `5500` agar sesuai konfigurasi CORS backend:

```bash
cd frontend
python3 -m http.server 5500
```

Buka:

```text
http://localhost:5500/login.html
```

## Endpoint API

Auth:

- `POST /api/auth/register`
- `POST /api/auth/login`

Movie:

- `GET /api/movies`
- `GET /api/movies?search=keyword`
- `GET /api/movies/{id}`
- `POST /api/movies`
- `PUT /api/movies/{id}`
- `DELETE /api/movies/{id}`
- `PATCH /api/movies/{id}/watch`

Semua endpoint `/api/movies/**` wajib menggunakan header:

```text
Authorization: Bearer <token>
```

## Contoh Request API

Register user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Budi Santoso","username":"budi","password":"password123"}'
```

Login user:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"budi","password":"password123"}'
```

Simpan token dari response login ke variable shell:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"budi","password":"password123"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
```

Get semua movie:

```bash
curl http://localhost:8080/api/movies \
  -H "Authorization: Bearer $TOKEN"
```

Search movie:

```bash
curl "http://localhost:8080/api/movies?search=inter" \
  -H "Authorization: Bearer $TOKEN"
```

Get movie by id:

```bash
curl http://localhost:8080/api/movies/1 \
  -H "Authorization: Bearer $TOKEN"
```

Create movie:

```bash
curl -X POST http://localhost:8080/api/movies \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Interstellar",
    "genre": "SCI_FI",
    "description": "A science fiction movie about space exploration.",
    "watched": true,
    "rating": 5,
    "posterUrl": "https://example.com/interstellar.jpg"
  }'
```

Update movie:

```bash
curl -X PUT http://localhost:8080/api/movies/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Interstellar",
    "genre": "SCI_FI",
    "description": "A science fiction movie about space exploration and family.",
    "watched": true,
    "rating": 5,
    "posterUrl": "https://example.com/interstellar.jpg"
  }'
```

Toggle watched/unwatched:

```bash
curl -X PATCH http://localhost:8080/api/movies/1/watch \
  -H "Authorization: Bearer $TOKEN"
```

Delete movie:

```bash
curl -X DELETE http://localhost:8080/api/movies/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Daftar Genre Enum

```text
ACTION
ADVENTURE
ANIMATION
COMEDY
CRIME
DOCUMENTARY
DRAMA
FAMILY
FANTASY
HORROR
MYSTERY
ROMANCE
SCI_FI
THRILLER
WAR
```

## Alur Penggunaan

1. Buka `register.html`, isi name, username, dan password.
2. Setelah register berhasil, user diarahkan ke `login.html`.
3. Login memakai username dan password.
4. Frontend menyimpan token login di LocalStorage.
5. Buka dashboard `index.html` untuk melihat daftar movie milik user login.
6. Klik `Tambah Movie`, pilih genre dari dropdown, isi rating 1 sampai 5, lalu simpan.
7. Gunakan search untuk mencari movie berdasarkan title.
8. Klik `Edit` untuk mengubah movie milik user login.
9. Klik `Mark Watched` atau `Mark Unwatched` untuk toggle status.
10. Klik `Delete` untuk menghapus movie.
11. Klik `Logout` untuk menghapus token dari LocalStorage.

## Bagian Penting

- Password user di-hash menggunakan BCrypt sebelum disimpan.
- Token login dibuat saat register atau login, lalu disimpan sementara di memory backend.
- Entity `Movie.genre` memakai enum `MovieGenre` dan disimpan sebagai string dengan `@Enumerated(EnumType.STRING)`.
- Query movie selalu memakai kombinasi `movie id` dan `current user`, sehingga user tidak bisa membaca, mengubah, atau menghapus movie milik user lain.
- Search hanya berdasarkan `title`.
- Error API memakai format `ErrorResponse` dengan `timestamp`, `status`, `error`, `message`, dan `path`.
