document.addEventListener('DOMContentLoaded', () => {
    requireAuth();

    const movieList = document.querySelector('#movieList');
    const searchForm = document.querySelector('#searchForm');
    const searchInput = document.querySelector('#searchInput');
    const logoutButton = document.querySelector('#logoutButton');
    const userLabel = document.querySelector('#userLabel');
    const message = document.querySelector('#message');
    const currentUser = getCurrentUser();

    if (currentUser) {
        userLabel.textContent = currentUser.name;
    }

    logoutButton.addEventListener('click', () => {
        clearAuth();
        window.location.href = 'login.html';
    });

    searchForm.addEventListener('submit', (event) => {
        event.preventDefault();
        loadMovies(searchInput.value.trim());
    });

    searchInput.addEventListener('input', () => {
        if (!searchInput.value.trim()) {
            loadMovies();
        }
    });

    movieList.addEventListener('click', async (event) => {
        const button = event.target.closest('button[data-action]');
        if (!button) {
            return;
        }

        const movieId = button.dataset.id;
        const action = button.dataset.action;

        if (action === 'edit') {
            window.location.href = `movie-form.html?id=${movieId}`;
            return;
        }

        if (action === 'delete') {
            const confirmed = window.confirm('Hapus movie ini?');
            if (!confirmed) {
                return;
            }

            try {
                await apiFetch(`/movies/${movieId}`, { method: 'DELETE' });
                await loadMovies(searchInput.value.trim());
            } catch (error) {
                setMessage(message, error.message);
            }
            return;
        }

        if (action === 'toggle') {
            try {
                await apiFetch(`/movies/${movieId}/watch`, { method: 'PATCH' });
                await loadMovies(searchInput.value.trim());
            } catch (error) {
                setMessage(message, error.message);
            }
        }
    });

    loadMovies();
});

async function loadMovies(search = '') {
    const movieList = document.querySelector('#movieList');
    const message = document.querySelector('#message');
    setMessage(message, '');
    movieList.innerHTML = '<p class="empty-state">Loading...</p>';

    try {
        const query = search ? `?search=${encodeURIComponent(search)}` : '';
        const movies = await apiFetch(`/movies${query}`);
        renderMovies(movies || []);
    } catch (error) {
        movieList.innerHTML = '';
        setMessage(message, error.message);
    }
}

function renderMovies(movies) {
    const movieList = document.querySelector('#movieList');

    if (!movies.length) {
        movieList.innerHTML = '<p class="empty-state">Movie tidak ditemukan.</p>';
        return;
    }

    movieList.innerHTML = movies.map((movie) => `
        <article class="movie-card">
            ${renderPoster(movie)}
            <div class="movie-content">
                <div class="movie-heading">
                    <h2>${escapeHtml(movie.title)}</h2>
                    <span class="badge ${movie.watched ? 'watched' : 'unwatched'}">
                        ${movie.watched ? 'Watched' : 'Unwatched'}
                    </span>
                </div>
                <p class="movie-meta">${formatGenre(movie.genre)} - Rating ${movie.rating}/5</p>
                <p class="movie-description">${escapeHtml(movie.description || 'Tidak ada deskripsi.')}</p>
                <div class="movie-actions">
                    <button class="button secondary" type="button" data-action="edit" data-id="${movie.id}">Edit</button>
                    <button class="button secondary" type="button" data-action="toggle" data-id="${movie.id}">
                        ${movie.watched ? 'Mark Unwatched' : 'Mark Watched'}
                    </button>
                    <button class="button danger" type="button" data-action="delete" data-id="${movie.id}">Delete</button>
                </div>
            </div>
        </article>
    `).join('');
}

function renderPoster(movie) {
    if (movie.posterUrl) {
        return `
            <div class="poster-wrap">
                <img src="${escapeAttribute(movie.posterUrl)}" alt="Poster ${escapeAttribute(movie.title)}" loading="lazy">
            </div>
        `;
    }

    return `
        <div class="poster-wrap poster-fallback">
            <span>${escapeHtml(movie.title.charAt(0).toUpperCase())}</span>
        </div>
    `;
}

function formatGenre(genre) {
    return String(genre || '')
        .replaceAll('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (char) => char.toUpperCase());
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function escapeAttribute(value) {
    return escapeHtml(value).replaceAll('`', '&#096;');
}
