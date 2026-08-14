const AdminNavigationItems = [
    { href: '/admin/account', label: 'Tài khoản', icon: 'bi-person-gear', roles: ['ADMIN'] },
    { href: '/admin/manager', label: 'Nhân viên', icon: 'bi-people', roles: ['ADMIN'] },
    { href: '/admin/department', label: 'Bộ phận', icon: 'bi-diagram-3', roles: ['ADMIN'] },
    { href: '/admin/article', label: 'Bài viết', icon: 'bi-newspaper', roles: ['ADMIN', 'ARTICLE'] },
    { href: '/admin/product', label: 'Sản phẩm', icon: 'bi-flower1', roles: ['ADMIN', 'PRODUCT'] },
    { href: '/admin/category', label: 'Danh mục', icon: 'bi-tags', roles: ['ADMIN', 'CATEGORY'] }
];

async function RenderAdminNavigation() {
    const Container = document.getElementById('AdminNavigation');
    if (!Container) {
        return;
    }

    const Roles = await LoadCurrentRoles();
    const CurrentPath = window.location.pathname;
    const CurrentItem = AdminNavigationItems.find(Item => Item.href === CurrentPath);
    if (CurrentItem && !CanSeeItem(CurrentItem, Roles)) {
        window.location.href = '/notfound';
        return;
    }

    const Links = AdminNavigationItems
        .filter(Item => CanSeeItem(Item, Roles))
        .map(Item => `
            <li class="nav-item">
                <a class="nav-link ${CurrentPath === Item.href ? 'active' : ''}" href="${Item.href}">
                    <i class="bi ${Item.icon}"></i>
                    <span>${Item.label}</span>
                </a>
            </li>
        `).join('');

    Container.innerHTML = `
        <nav class="navbar navbar-expand-xl navbar-dark shadow-sm admin-navbar">
            <div class="container page-shell">
                <a class="navbar-brand fw-semibold" href="/">Hoa Giấy Phù Đổng</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#AdminNavbar"
                        aria-controls="AdminNavbar" aria-expanded="false" aria-label="Mở menu quản trị">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="AdminNavbar">
                    <ul class="navbar-nav ms-auto align-items-xl-center admin-nav-actions">
                        ${Links}
                        <li class="nav-item ms-xl-2">
                            <button class="btn btn-outline-light btn-sm" type="button" onclick="LogoutJwt()">
                                    <i class="bi bi-box-arrow-right"></i>
                                    <span>Đăng xuất</span>
                            </button>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    `;
}

async function LoadCurrentRoles() {
    try {
        const Response = await fetch('/api/auth/account', {
            credentials: 'same-origin',
            headers: BuildAuthHeader()
        });
        if (Response.status === 401) {
            RedirectToLogin();
            return [];
        }
        if (Response.status === 403) {
            window.location.href = '/notfound';
            return [];
        }

        const Payload = await Response.json();
        const Data = Payload.data || {};
        return Data.roles || [];
    } catch (Error) {
        RedirectToLogin();
        return [];
    }
}

function CanSeeItem(Item, Roles) {
    return Item.roles.some(Role => Roles.includes(Role));
}

RenderAdminNavigation();
