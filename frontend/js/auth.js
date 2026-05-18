document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.querySelector('#loginForm');
    const registerForm = document.querySelector('#registerForm');
    const message = document.querySelector('#message');
    const params = new URLSearchParams(window.location.search);

    if (params.get('registered') === 'true') {
        setMessage(message, 'Registrasi berhasil. Silakan login.', 'success');
    }

    if (getToken() && loginForm) {
        window.location.href = 'index.html';
        return;
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            setMessage(message, '');

            const payload = {
                username: loginForm.username.value.trim(),
                password: loginForm.password.value
            };

            try {
                const auth = await apiFetch('/auth/login', {
                    method: 'POST',
                    body: JSON.stringify(payload),
                    skipAuth: true
                });
                saveAuth(auth);
                window.location.href = 'index.html';
            } catch (error) {
                setMessage(message, error.message);
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            setMessage(message, '');

            const payload = {
                name: registerForm.name.value.trim(),
                username: registerForm.username.value.trim(),
                password: registerForm.password.value
            };

            try {
                await apiFetch('/auth/register', {
                    method: 'POST',
                    body: JSON.stringify(payload),
                    skipAuth: true
                });
                window.location.href = 'login.html?registered=true';
            } catch (error) {
                setMessage(message, error.message);
            }
        });
    }
});
