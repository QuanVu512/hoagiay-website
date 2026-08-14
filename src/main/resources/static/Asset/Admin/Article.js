const ApiUrl = '/api/article';

const State = {
    Articles: [],
    Page: 0,
    Size: 10,
    Sort: 'id,desc',
    Filters: {},
    PageData: null,
    EditingId: null
};

const ArticleModal = new bootstrap.Modal(document.getElementById('ArticleModal'));
const ArticleForm = document.getElementById('ArticleForm');
const ArticleTableBody = document.getElementById('ArticleTableBody');
const AlertBox = document.getElementById('AlertBox');

document.getElementById('AddArticleButton').addEventListener('click', OpenCreateModal);
ArticleForm.addEventListener('submit', SaveArticle);
BindAdminListControls('ArticleSort', 'ArticleSize', State, LoadArticles);
BindAdminFilterControls({
    searchId: 'ArticleKeyword',
    applyButtonId: 'ArticleSearchButton',
    resetButtonId: 'ArticleResetButton',
    filters: [
        { id: 'ArticlePublishedFilter', key: 'published' }
    ]
}, State, LoadArticles);

LoadArticles();

async function LoadArticles() {
    try {
        const Result = await ApiRequest(BuildAdminPageUrl(ApiUrl, State));
        ApplyAdminPage(State, 'Articles', Result.data);
        RenderArticles();
        RenderAdminPagination('ArticlePagination', State.PageData, Page => {
            State.Page = Page;
            LoadArticles();
        });
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
        RenderArticles();
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

function RenderArticles() {
    if (!State.Articles.length) {
        ArticleTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-secondary py-4">Chưa có bài viết.</td>
            </tr>
        `;
        return;
    }

    ArticleTableBody.innerHTML = State.Articles.map(Article => `
        <tr>
            <td class="ps-4 fw-semibold">${Article.id}</td>
            <td class="fw-medium">${EscapeHtml(Article.title)}</td>
            <td><code>${EscapeHtml(Article.slug)}</code></td>
            <td>
                <span class="status-dot ${Article.published ? 'bg-success' : 'bg-secondary'}"></span>
                ${Article.published ? 'Đã xuất bản' : 'Bản nháp'}
            </td>
            <td>${FormatDateTime(Article.publishedAt)}</td>
            <td class="text-end pe-4">
                <button class="btn btn-outline-primary btn-sm icon-btn me-1" type="button" title="Sửa"
                        onclick="OpenEditModal(${Article.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm icon-btn" type="button" title="Xóa"
                        onclick="DeleteArticle(${Article.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function OpenCreateModal() {
    State.EditingId = null;
    ArticleForm.reset();
    document.getElementById('ArticleId').value = '';
    document.getElementById('ArticleModalTitle').textContent = 'Thêm bài viết';
    document.getElementById('Published').checked = false;
    ArticleModal.show();
}

function OpenEditModal(Id) {
    const Article = State.Articles.find(Item => Item.id === Id);
    if (!Article) {
        ShowAlert('Không tìm thấy bài viết cần sửa.', 'danger');
        return;
    }

    State.EditingId = Id;
    document.getElementById('ArticleId').value = Id;
    document.getElementById('ArticleModalTitle').textContent = 'Sửa bài viết';
    document.getElementById('Title').value = Article.title;
    document.getElementById('Slug').value = Article.slug || '';
    document.getElementById('Summary').value = Article.summary || '';
    document.getElementById('Content').value = Article.content || '';
    document.getElementById('ThumbnailUrl').value = Article.thumbnailUrl || '';
    document.getElementById('Published').checked = Article.published;
    ArticleModal.show();
}

async function SaveArticle(Event) {
    Event.preventDefault();

    const Payload = {
        title: document.getElementById('Title').value.trim(),
        slug: document.getElementById('Slug').value.trim(),
        summary: document.getElementById('Summary').value.trim(),
        content: document.getElementById('Content').value.trim(),
        thumbnailUrl: document.getElementById('ThumbnailUrl').value.trim(),
        published: document.getElementById('Published').checked
    };

    try {
        const Result = await ApiRequest(State.EditingId ? `${ApiUrl}/${State.EditingId}` : ApiUrl, {
            method: State.EditingId ? 'PUT' : 'POST',
            body: JSON.stringify(Payload)
        });

        ShowAlert(Result.message, 'success');
        ArticleModal.hide();
        await LoadArticles();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

async function DeleteArticle(Id) {
    if (!confirm('Xóa bài viết này?')) {
        return;
    }

    try {
        const Result = await ApiRequest(`${ApiUrl}/${Id}`, { method: 'DELETE' });
        ShowAlert(Result.message, 'success');
        await LoadArticles();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

function ShowAlert(Message, Type) {
    AlertBox.className = `alert alert-${Type}`;
    AlertBox.textContent = Message;
}

function FormatDateTime(Value) {
    if (!Value) {
        return '';
    }

    return new Intl.DateTimeFormat('vi-VN', {
        dateStyle: 'short',
        timeStyle: 'short'
    }).format(new Date(Value));
}

function EscapeHtml(Value) {
    return String(Value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
