const ApiUrl = '/api/manager';
const DepartmentUrl = '/api/department/option';

const State = {
    Managers: [],
    Departments: [],
    Page: 0,
    Size: 10,
    Sort: 'id,asc',
    Filters: {},
    PageData: null,
    EditingId: null
};

const ManagerModal = new bootstrap.Modal(document.getElementById('ManagerModal'));
const ManagerForm = document.getElementById('ManagerForm');
const ManagerTableBody = document.getElementById('ManagerTableBody');
const AlertBox = document.getElementById('AlertBox');

document.getElementById('AddManagerButton').addEventListener('click', OpenCreateModal);
ManagerForm.addEventListener('submit', SaveManager);
BindAdminListControls('ManagerSort', 'ManagerSize', State, LoadManagers);
BindAdminFilterControls({
    searchId: 'ManagerKeyword',
    applyButtonId: 'ManagerSearchButton',
    resetButtonId: 'ManagerResetButton',
    filters: [
        { id: 'ManagerDepartmentFilter', key: 'departmentId' },
        { id: 'ManagerHasAccountFilter', key: 'hasAccount' }
    ]
}, State, LoadManagers);

LoadManagers();

async function LoadManagers() {
    try {
        const [ManagersResult, DepartmentsResult] = await Promise.all([
            ApiRequest(BuildAdminPageUrl(ApiUrl, State)),
            ApiRequest(DepartmentUrl)
        ]);

        ApplyAdminPage(State, 'Managers', ManagersResult.data);
        State.Departments = DepartmentsResult.data;
        RenderManagers();
        RenderAdminPagination('ManagerPagination', State.PageData, Page => {
            State.Page = Page;
            LoadManagers();
        });
        RenderDepartmentFilterOptions();
        RenderDepartmentOptions();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
        RenderManagers();
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

function RenderManagers() {
    if (!State.Managers.length) {
        ManagerTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-secondary py-4">Chưa có hồ sơ nhân viên.</td>
            </tr>
        `;
        return;
    }

    ManagerTableBody.innerHTML = State.Managers.map(Manager => `
        <tr>
            <td class="ps-4 fw-semibold">${Manager.id}</td>
            <td>${EscapeHtml(Manager.fullName)}</td>
            <td>${EscapeHtml(Manager.phone || '')}</td>
            <td>${EscapeHtml(Manager.departmentName || '')}</td>
            <td>${EscapeHtml(BuildAddress(Manager))}</td>
            <td>
                ${Manager.hasAccount
                    ? `<span class="badge text-bg-success">${EscapeHtml(Manager.username)}</span>`
                    : '<span class="badge text-bg-light border">Chưa có</span>'}
            </td>
            <td class="text-end pe-4">
                <button class="btn btn-outline-primary btn-sm icon-btn me-1" type="button" title="Sửa"
                        onclick="OpenEditModal(${Manager.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm icon-btn" type="button" title="Xóa"
                        ${Manager.hasAccount ? 'disabled' : ''} onclick="DeleteManager(${Manager.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function RenderDepartmentOptions(SelectedId = '') {
    const Options = State.Departments.map(Department => `
        <option value="${Department.id}" ${String(Department.id) === String(SelectedId) ? 'selected' : ''}>
            ${EscapeHtml(Department.name)}${Department.active ? '' : ' (tạm khóa)'}
        </option>
    `).join('');

    document.getElementById('DepartmentId').innerHTML = '<option value="">Chọn bộ phận...</option>' + Options;
}

function RenderDepartmentFilterOptions() {
    const FilterSelect = document.getElementById('ManagerDepartmentFilter');
    if (!FilterSelect) {
        return;
    }

    const CurrentValue = FilterSelect.value;
    FilterSelect.innerHTML = '<option value="">Tất cả bộ phận</option>' + State.Departments.map(Department => `
        <option value="${Department.id}">${EscapeHtml(Department.name)}${Department.active ? '' : ' (tạm khóa)'}</option>
    `).join('');
    FilterSelect.value = CurrentValue;
}

function OpenCreateModal() {
    State.EditingId = null;
    ManagerForm.reset();
    document.getElementById('ManagerId').value = '';
    document.getElementById('ManagerModalTitle').textContent = 'Thêm hồ sơ nhân viên';
    RenderDepartmentOptions();
    ManagerModal.show();
}

function OpenEditModal(Id) {
    const Manager = State.Managers.find(Item => Item.id === Id);
    if (!Manager) {
        ShowAlert('Không tìm thấy hồ sơ cần sửa.', 'danger');
        return;
    }

    State.EditingId = Id;
    document.getElementById('ManagerId').value = Id;
    document.getElementById('ManagerModalTitle').textContent = 'Sửa hồ sơ nhân viên';
    document.getElementById('FullName').value = Manager.fullName;
    document.getElementById('Phone').value = Manager.phone || '';
    RenderDepartmentOptions(Manager.departmentId || '');
    document.getElementById('AddressDetail').value = Manager.addressDetail || '';
    document.getElementById('Ward').value = Manager.ward || '';
    document.getElementById('District').value = Manager.district || '';
    document.getElementById('Province').value = Manager.province || '';
    ManagerModal.show();
}

async function SaveManager(Event) {
    Event.preventDefault();

    const Payload = {
        fullName: document.getElementById('FullName').value.trim(),
        phone: document.getElementById('Phone').value.trim(),
        departmentId: document.getElementById('DepartmentId').value
            ? Number(document.getElementById('DepartmentId').value)
            : null,
        addressDetail: document.getElementById('AddressDetail').value.trim(),
        ward: document.getElementById('Ward').value.trim(),
        district: document.getElementById('District').value.trim(),
        province: document.getElementById('Province').value.trim()
    };

    try {
        const Result = await ApiRequest(State.EditingId ? `${ApiUrl}/${State.EditingId}` : ApiUrl, {
            method: State.EditingId ? 'PUT' : 'POST',
            body: JSON.stringify(Payload)
        });

        ShowAlert(Result.message, 'success');
        ManagerModal.hide();
        await LoadManagers();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

async function DeleteManager(Id) {
    if (!confirm('Xóa hồ sơ nhân viên này?')) {
        return;
    }

    try {
        const Result = await ApiRequest(`${ApiUrl}/${Id}`, { method: 'DELETE' });
        ShowAlert(Result.message, 'success');
        await LoadManagers();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

function ShowAlert(Message, Type) {
    AlertBox.className = `alert alert-${Type}`;
    AlertBox.textContent = Message;
}

function BuildAddress(Manager) {
    return [
        Manager.addressDetail,
        Manager.ward,
        Manager.district,
        Manager.province
    ].filter(Boolean).join(', ');
}

function EscapeHtml(Value) {
    return String(Value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
