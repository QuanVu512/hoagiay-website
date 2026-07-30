const AdminNavigationItems = [
    { href: '/admin/account', label: 'Tài khoản', icon: 'bi-person-gear' },
    { href: '/admin/manager', label: 'Nhân viên', icon: 'bi-people' },
    { href: '/admin/department', label: 'Bộ phận', icon: 'bi-diagram-3' },
    { href: '/admin/article', label: 'Bài viết', icon: 'bi-newspaper' },
    { href: '/admin/product', label: 'Sản phẩm', icon: 'bi-flower1' },
    { href: '/admin/category', label: 'Danh mục', icon: 'bi-tags' }
];

function RenderAdminNavigation() {
    const Container = document.getElementById('AdminNavigation');
    if (!Container) {
        return;
    }

    const CurrentPath = window.location.pathname;
    const Links = AdminNavigationItems.map(Item => `
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
                            <form action="/logout" method="post">
                                <input data-csrf-token name="_csrf" type="hidden">
                                <button class="btn btn-outline-light btn-sm" type="submit">
                                    <i class="bi bi-box-arrow-right"></i>
                                    <span>Đăng xuất</span>
                                </button>
                            </form>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    `;
}

RenderAdminNavigation();
