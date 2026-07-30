const ApiUrl = '/api/department';

const State = {
    Departments: [],
    EditingId: null
};

const DepartmentModal = new bootstrap.Modal(document.getElementById('DepartmentModal'));
const DepartmentForm = document.getElementById('DepartmentForm');
const DepartmentTableBody = document.getElementById('DepartmentTableBody');
const AlertBox = document.getElementById('AlertBox');

document.getElementById('AddDepartmentButton').addEventListener('click', OpenCreateModal);
DepartmentForm.addEventListener('submit', SaveDepartment);

LoadDepartments();

async function LoadDepartments() {
    try {
        const Result = await ApiRequest(ApiUrl);
        State.Departments = Result.data;
        RenderDepartments();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
        RenderDepartments();
    }
}

async function ApiRequest(Url, Options = {}) {
    const Response = await fetch(Url, {
        headers: {
            'Content-Type': 'application/json',
            ...BuildCsrfHeader(),
            ...(Options.headers || {})
        },
        ...Options
    });
    const Payload = await Response.json().catch(() => ({}));

    if (!Response.ok) {
        throw new Error(Payload.message || 'Không thể xử lý yêu cầu.');
    }

    return Payload;
}

function BuildCsrfHeader() {
    const Token = document.cookie
        .split('; ')
        .find(Item => Item.startsWith('XSRF-TOKEN='))
        ?.split('=')
        .slice(1)
        .join('=');

    return Token ? { 'X-XSRF-TOKEN': decodeURIComponent(Token) } : {};
}

function RenderDepartments() {
    if (!State.Departments.length) {
        DepartmentTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-secondary py-4">Chưa có bộ phận.</td>
            </tr>
        `;
        return;
    }

    DepartmentTableBody.innerHTML = State.Departments.map(Department => `
        <tr>
            <td class="ps-4 fw-semibold">${Department.id}</td>
            <td class="fw-medium">${EscapeHtml(Department.name)}</td>
            <td>${EscapeHtml(Department.description || '')}</td>
            <td>
                <span class="status-dot ${Department.active ? 'bg-success' : 'bg-secondary'}"></span>
                ${Department.active ? 'Đang hoạt động' : 'Tạm khóa'}
            </td>
            <td class="text-end pe-4">
                <button class="btn btn-outline-primary btn-sm icon-btn me-1" type="button" title="Sửa"
                        onclick="OpenEditModal(${Department.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm icon-btn" type="button" title="Xóa"
                        onclick="DeleteDepartment(${Department.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function OpenCreateModal() {
    State.EditingId = null;
    DepartmentForm.reset();
    document.getElementById('DepartmentId').value = '';
    document.getElementById('DepartmentModalTitle').textContent = 'Thêm bộ phận';
    document.getElementById('Active').checked = true;
    DepartmentModal.show();
}

function OpenEditModal(Id) {
    const Department = State.Departments.find(Item => Item.id === Id);
    if (!Department) {
        ShowAlert('Không tìm thấy bộ phận cần sửa.', 'danger');
        return;
    }

    State.EditingId = Id;
    document.getElementById('DepartmentId').value = Id;
    document.getElementById('DepartmentModalTitle').textContent = 'Sửa bộ phận';
    document.getElementById('Name').value = Department.name;
    document.getElementById('Description').value = Department.description || '';
    document.getElementById('Active').checked = Department.active;
    DepartmentModal.show();
}

async function SaveDepartment(Event) {
    Event.preventDefault();

    const Payload = {
        name: document.getElementById('Name').value.trim(),
        description: document.getElementById('Description').value.trim(),
        active: document.getElementById('Active').checked
    };

    try {
        const Result = await ApiRequest(State.EditingId ? `${ApiUrl}/${State.EditingId}` : ApiUrl, {
            method: State.EditingId ? 'PUT' : 'POST',
            body: JSON.stringify(Payload)
        });

        ShowAlert(Result.message, 'success');
        DepartmentModal.hide();
        await LoadDepartments();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

async function DeleteDepartment(Id) {
    if (!confirm('Xóa bộ phận này?')) {
        return;
    }

    try {
        const Result = await ApiRequest(`${ApiUrl}/${Id}`, { method: 'DELETE' });
        ShowAlert(Result.message, 'success');
        await LoadDepartments();
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
