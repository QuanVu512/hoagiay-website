const AccessTokenStorageKey = 'HoaGiayPhuDongAccessToken';
const RefreshTokenStorageKey = 'HoaGiayPhuDongRefreshToken';

function ClearLegacyAuthTokens() {
    localStorage.removeItem(AccessTokenStorageKey);
    localStorage.removeItem(RefreshTokenStorageKey);
}

function SaveAuthTokens() {
    ClearLegacyAuthTokens();
}

function ClearAuthTokens() {
    ClearLegacyAuthTokens();
}

function BuildAuthHeader() {
    return {};
}

async function LogoutJwt() {
    try {
        await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'same-origin'
        });
    } finally {
        ClearAuthTokens();
        window.location.href = '/login?logout';
    }
}

function RedirectToLogin() {
    ClearAuthTokens();
    window.location.href = '/login?error';
}
