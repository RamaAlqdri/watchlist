const MOVIE_GENRES = [
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
];

document.addEventListener('DOMContentLoaded', () => {
    requireAuth();

    const movieForm = document.querySelector('#movieForm');
    const genreSelect = document.querySelector('#genre');
    const formTitle = document.querySelector('#formTitle');
    const message = document.querySelector('#message');
    const logoutButton = document.querySelector('#logoutButton');
    const params = new URLSearchParams(window.location.search);
    const movieId = params.get('id');

    renderGenreOptions(genreSelect);

    logoutButton.addEventListener('click', () => {
        clearAuth();
        window.location.href = 'login.html';
    });

    if (movieId) {
        formTitle.textContent = 'Edit Movie';
        loadMovie(movieId);
    }

    movieForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        setMessage(message, '');

        const payload = {
            title: movieForm.title.value.trim(),
            genre: movieForm.genre.value,
            description: movieForm.description.value.trim(),
            watched: movieForm.watched.checked,
            rating: Number(movieForm.rating.value),
            posterUrl: movieForm.posterUrl.value.trim()
        };

        try {
            const path = movieId ? `/movies/${movieId}` : '/movies';
            const method = movieId ? 'PUT' : 'POST';
            await apiFetch(path, {
                method,
                body: JSON.stringify(payload)
            });
            window.location.href = 'index.html';
        } catch (error) {
            setMessage(message, error.message);
        }
    });
});

function renderGenreOptions(selectElement) {
    selectElement.innerHTML = [
        '<option value="" disabled selected>Pilih genre</option>',
        ...MOVIE_GENRES.map((genre) => `<option value="${genre}">${formatGenreLabel(genre)}</option>`)
    ].join('');
}

async function loadMovie(movieId) {
    const movieForm = document.querySelector('#movieForm');
    const message = document.querySelector('#message');

    try {
        const movie = await apiFetch(`/movies/${movieId}`);
        movieForm.title.value = movie.title;
        movieForm.genre.value = movie.genre;
        movieForm.description.value = movie.description || '';
        movieForm.watched.checked = movie.watched;
        movieForm.rating.value = movie.rating;
        movieForm.posterUrl.value = movie.posterUrl || '';
    } catch (error) {
        setMessage(message, error.message);
    }
}

function formatGenreLabel(genre) {
    return genre
        .replaceAll('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (char) => char.toUpperCase());
}
