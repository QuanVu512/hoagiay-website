const ApiUrl = '/api/product';
const CategoryUrl = '/api/categories';

const State = {
    Products: [],
    Categories: [],
    Page: 0,
    Size: 10,
    Sort: 'id,desc',
    Filters: {},
    PageData: null,
    EditingId: null
};

const ProductModal = new bootstrap.Modal(document.getElementById('ProductModal'));
const ProductForm = document.getElementById('ProductForm');
const ProductTableBody = document.getElementById('ProductTableBody');
const AlertBox = document.getElementById('AlertBox');

document.getElementById('AddProductButton').addEventListener('click', OpenCreateModal);
ProductForm.addEventListener('submit', SaveProduct);
BindAdminListControls('ProductSort', 'ProductSize', State, LoadProducts);
BindAdminFilterControls({
    searchId: 'ProductKeyword',
    applyButtonId: 'ProductSearchButton',
    resetButtonId: 'ProductResetButton',
    filters: [
        { id: 'ProductCategoryFilter', key: 'categoryId' },
        { id: 'ProductInventoryFilter', key: 'inventoryStatus' },
        { id: 'ProductFeaturedFilter', key: 'featured' }
    ]
}, State, LoadProducts);

LoadProducts();

async function LoadProducts() {
    try {
        const [ProductsResult, CategoriesResult] = await Promise.all([
            ApiRequest(BuildAdminPageUrl(ApiUrl, State)),
            ApiRequest(CategoryUrl, { headers: {} }, false)
        ]);

        ApplyAdminPage(State, 'Products', ProductsResult.data);
        State.Categories = CategoriesResult;
        RenderProducts();
        RenderAdminPagination('ProductPagination', State.PageData, Page => {
            State.Page = Page;
            LoadProducts();
        });
        RenderCategoryFilterOptions();
        RenderCategoryOptions();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
        RenderProducts();
    }
}

async function ApiRequest(Url, Options = {}, Wrapped = true) {
    const Response = await fetch(Url, {
        ...Options,
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json',
            ...BuildAuthHeader(),
            ...(Options.headers || {})
        }
    });
    const Payload = await Response.json().catch(() => (Wrapped ? {} : []));

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

function RenderProducts() {
    if (!State.Products.length) {
        ProductTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-secondary py-4">Chưa có sản phẩm.</td>
            </tr>
        `;
        return;
    }

    ProductTableBody.innerHTML = State.Products.map(Product => `
        <tr>
            <td class="ps-4 fw-semibold">${Product.id}</td>
            <td>
                <div class="fw-medium">${EscapeHtml(Product.name)}</div>
                <div class="text-secondary small">${EscapeHtml(Product.code || Product.slug)}</div>
            </td>
            <td>${EscapeHtml(Product.categoryName || '')}</td>
            <td>${EscapeHtml(Product.priceLabel || '')}</td>
            <td>${EscapeHtml(Product.inventoryStatusLabel || Product.inventoryStatus || '')}</td>
            <td>
                <span class="badge ${Product.featured ? 'text-bg-success' : 'text-bg-light border'}">
                    ${Product.featured ? 'Có' : 'Không'}
                </span>
            </td>
            <td class="text-end pe-4">
                <button class="btn btn-outline-primary btn-sm icon-btn me-1" type="button" title="Sửa"
                        onclick="OpenEditModal(${Product.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm icon-btn" type="button" title="Xóa"
                        onclick="DeleteProduct(${Product.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function RenderCategoryOptions(SelectedId = '') {
    const Options = State.Categories.map(Category => `
        <option value="${Category.id}" ${String(Category.id) === String(SelectedId) ? 'selected' : ''}>
            ${EscapeHtml(Category.name)}
        </option>
    `).join('');

    document.getElementById('CategoryId').innerHTML = '<option value="">Chưa gán danh mục</option>' + Options;
}

function RenderCategoryFilterOptions() {
    const FilterSelect = document.getElementById('ProductCategoryFilter');
    if (!FilterSelect) {
        return;
    }

    const CurrentValue = FilterSelect.value;
    FilterSelect.innerHTML = '<option value="">Tất cả danh mục</option>' + State.Categories.map(Category => `
        <option value="${Category.id}">${EscapeHtml(Category.name)}</option>
    `).join('');
    FilterSelect.value = CurrentValue;
}

function OpenCreateModal() {
    State.EditingId = null;
    ProductForm.reset();
    document.getElementById('ProductId').value = '';
    document.getElementById('ProductModalTitle').textContent = 'Thêm sản phẩm';
    document.getElementById('InventoryStatus').value = 'AVAILABLE';
    document.getElementById('Featured').checked = false;
    RenderCategoryOptions();
    ProductModal.show();
}

function OpenEditModal(Id) {
    const Product = State.Products.find(Item => Item.id === Id);
    if (!Product) {
        ShowAlert('Không tìm thấy sản phẩm cần sửa.', 'danger');
        return;
    }

    State.EditingId = Id;
    document.getElementById('ProductId').value = Id;
    document.getElementById('ProductModalTitle').textContent = 'Sửa sản phẩm';
    document.getElementById('Name').value = Product.name;
    document.getElementById('Code').value = Product.code || '';
    RenderCategoryOptions(Product.categoryId || '');
    document.getElementById('Slug').value = Product.slug || '';
    document.getElementById('ShortDescription').value = Product.shortDescription || '';
    document.getElementById('PriceAmount').value = Product.priceAmount ?? '';
    document.getElementById('PriceLabel').value = Product.priceLabel || '';
    document.getElementById('InventoryStatus').value = Product.inventoryStatus || 'AVAILABLE';
    document.getElementById('ColorFamily').value = Product.colorFamily || '';
    document.getElementById('HeightCm').value = Product.heightCm ?? '';
    document.getElementById('ThumbnailUrl').value = Product.thumbnailUrl || '';
    document.getElementById('Featured').checked = Product.featured;
    ProductModal.show();
}

async function SaveProduct(Event) {
    Event.preventDefault();

    const PriceValue = document.getElementById('PriceAmount').value;
    const HeightValue = document.getElementById('HeightCm').value;
    const CategoryValue = document.getElementById('CategoryId').value;
    const Payload = {
        categoryId: CategoryValue ? Number(CategoryValue) : null,
        name: document.getElementById('Name').value.trim(),
        slug: document.getElementById('Slug').value.trim(),
        code: document.getElementById('Code').value.trim(),
        shortDescription: document.getElementById('ShortDescription').value.trim(),
        priceAmount: PriceValue ? Number(PriceValue) : null,
        priceLabel: document.getElementById('PriceLabel').value.trim(),
        colorFamily: document.getElementById('ColorFamily').value.trim(),
        heightCm: HeightValue ? Number(HeightValue) : null,
        inventoryStatus: document.getElementById('InventoryStatus').value,
        thumbnailUrl: document.getElementById('ThumbnailUrl').value.trim(),
        featured: document.getElementById('Featured').checked
    };

    try {
        const Result = await ApiRequest(State.EditingId ? `${ApiUrl}/${State.EditingId}` : ApiUrl, {
            method: State.EditingId ? 'PUT' : 'POST',
            body: JSON.stringify(Payload)
        });

        ShowAlert(Result.message, 'success');
        ProductModal.hide();
        await LoadProducts();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

async function DeleteProduct(Id) {
    if (!confirm('Xóa sản phẩm này?')) {
        return;
    }

    try {
        const Result = await ApiRequest(`${ApiUrl}/${Id}`, { method: 'DELETE' });
        ShowAlert(Result.message, 'success');
        await LoadProducts();
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
