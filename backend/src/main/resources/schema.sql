CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    genre VARCHAR(30) NOT NULL,
    description TEXT,
    watched BOOLEAN NOT NULL DEFAULT FALSE,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    poster_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_movies_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_movies_genre
        CHECK (genre IN (
            'ACTION',
            'ADVENTURE',
            'ANIMATION',
            'COMEDY',
            'CRIME',
            'DOCUMENTARY',
            'DRAMA',
            'FAMILY',
            'FANTASY',
            'HORROR',
            'MYSTERY',
            'ROMANCE',
            'SCI_FI',
            'THRILLER',
            'WAR'
        ))
);

CREATE INDEX IF NOT EXISTS idx_movies_user_id ON movies (user_id);
CREATE INDEX IF NOT EXISTS idx_movies_user_title ON movies (user_id, title);
