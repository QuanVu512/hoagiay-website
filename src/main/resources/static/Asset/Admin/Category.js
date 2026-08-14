const ApiUrl = '/api/category';

const State = {
    Categories: [],
    Page: 0,
    Size: 10,
    Sort: 'sortOrder,asc;id,asc',
    Filters: {},
    PageData: null,
    EditingId: null
};

const CategoryModal = new bootstrap.Modal(document.getElementById('CategoryModal'));
const CategoryForm = document.getElementById('CategoryForm');
const CategoryTableBody = document.getElementById('CategoryTableBody');
const AlertBox = document.getElementById('AlertBox');

document.getElementById('AddCategoryButton').addEventListener('click', OpenCreateModal);
CategoryForm.addEventListener('submit', SaveCategory);
BindAdminListControls('CategorySort', 'CategorySize', State, LoadCategories);
BindAdminFilterControls({
    searchId: 'CategoryKeyword',
    applyButtonId: 'CategorySearchButton',
    resetButtonId: 'CategoryResetButton',
    filters: [
        { id: 'CategoryActiveFilter', key: 'active' }
    ]
}, State, LoadCategories);

LoadCategories();

async function LoadCategories() {
    try {
        const Result = await ApiRequest(BuildAdminPageUrl(ApiUrl, State));
        ApplyAdminPage(State, 'Categories', Result.data);
        RenderCategories();
        RenderAdminPagination('CategoryPagination', State.PageData, Page => {
            State.Page = Page;
            LoadCategories();
        });
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
        RenderCategories();
    }
}

async function ApiRequest(Url, Options = {}) {
    const Response = await fetch(Url, {
        ...Options,
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json',
            ...BuildAuthHeader(),
            ...(Options.headers || {})
        }
    });
    const Payload = await Response.json().catch(() => ({}));

    if (Response.status === 401) {
        RedirectToLogin();
        throw new Error('Can dang nhap lai.');
    }
    if (Response.status === 403) {
        window.location.href = '/notfound';
        throw new Error('Khong co quyen truy cap.');
    }

    if (!Response.ok) {
        throw new Error(Payload.message || 'Không thể xử lý yêu cầu.');
    }

    return Payload;
}

function RenderCategories() {
    if (!State.Categories.length) {
        CategoryTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-secondary py-4">Chưa có danh mục.</td>
            </tr>
        `;
        return;
    }

    CategoryTableBody.innerHTML = State.Categories.map(Category => `
        <tr>
            <td class="ps-4 fw-semibold">${Category.id}</td>
            <td>${Category.sortOrder ?? 0}</td>
            <td class="fw-medium">${EscapeHtml(Category.name)}</td>
            <td><code>${EscapeHtml(Category.slug)}</code></td>
            <td>
                <span class="status-dot ${Category.active ? 'bg-success' : 'bg-secondary'}"></span>
                ${Category.active ? 'Đang hiển thị' : 'Tạm ẩn'}
            </td>
            <td class="text-end pe-4">
                <button class="btn btn-outline-primary btn-sm icon-btn me-1" type="button" title="Sửa"
                        onclick="OpenEditModal(${Category.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm icon-btn" type="button" title="Xóa"
                        onclick="DeleteCategory(${Category.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function OpenCreateModal() {
    State.EditingId = null;
    CategoryForm.reset();
    document.getElementById('CategoryId').value = '';
    document.getElementById('CategoryModalTitle').textContent = 'Thêm danh mục';
    document.getElementById('SortOrder').value = 0;
    document.getElementById('Active').checked = true;
    CategoryModal.show();
}

function OpenEditModal(Id) {
    const Category = State.Categories.find(Item => Item.id === Id);
    if (!Category) {
        ShowAlert('Không tìm thấy danh mục cần sửa.', 'danger');
        return;
    }

    State.EditingId = Id;
    document.getElementById('CategoryId').value = Id;
    document.getElementById('CategoryModalTitle').textContent = 'Sửa danh mục';
    document.getElementById('Name').value = Category.name;
    document.getElementById('Slug').value = Category.slug || '';
    document.getElementById('Description').value = Category.description || '';
    document.getElementById('SortOrder').value = Category.sortOrder ?? 0;
    document.getElementById('Active').checked = Category.active;
    CategoryModal.show();
}

async function SaveCategory(Event) {
    Event.preventDefault();

    const Payload = {
        name: document.getElementById('Name').value.trim(),
        slug: document.getElementById('Slug').value.trim(),
        description: document.getElementById('Description').value.trim(),
        sortOrder: Number(document.getElementById('SortOrder').value || 0),
        active: document.getElementById('Active').checked
    };

    try {
        const Result = await ApiRequest(State.EditingId ? `${ApiUrl}/${State.EditingId}` : ApiUrl, {
            method: State.EditingId ? 'PUT' : 'POST',
            body: JSON.stringify(Payload)
        });

        ShowAlert(Result.message, 'success');
        CategoryModal.hide();
        await LoadCategories();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

async function DeleteCategory(Id) {
    if (!confirm('Xóa danh mục này?')) {
        return;
    }

    try {
        const Result = await ApiRequest(`${ApiUrl}/${Id}`, { method: 'DELETE' });
        ShowAlert(Result.message, 'success');
        await LoadCategories();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

function ShowAlert(Message, Type) {
    AlertBox.className = `alert alert-${Type}`;
    AlertBox.textContent = Message;
}

function EscapeHtml(Value) {
    return String(Value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
