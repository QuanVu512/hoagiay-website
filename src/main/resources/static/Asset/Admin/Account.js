const ApiUrl = '/api/account';
const AvailableManagerUrl = '/api/available/manager';

const State = {
    Accounts: [],
    Roles: [],
    AvailableManagers: [],
    EditingId: null
};

const AccountModal = new bootstrap.Modal(document.getElementById('AccountModal'));
const AccountForm = document.getElementById('AccountForm');
const AccountTableBody = document.getElementById('AccountTableBody');
const AlertBox = document.getElementById('AlertBox');
const ManagerSelect = document.getElementById('ManagerId');
const RoleSelect = document.getElementById('RoleIds');
const CreateFields = [...document.querySelectorAll('.CreateField')];

document.getElementById('AddAccountButton').addEventListener('click', OpenCreateModal);
ManagerSelect.addEventListener('change', SyncCreateFieldState);
AccountForm.addEventListener('submit', SaveAccount);

LoadPage();

async function LoadPage() {
    try {
        const [AccountsResult, RolesResult, ManagersResult] = await Promise.all([
            ApiRequest(ApiUrl),
            ApiRequest(`${ApiUrl}/role`),
            ApiRequest(AvailableManagerUrl)
        ]);

        State.Accounts = AccountsResult.data;
        State.Roles = RolesResult.data;
        State.AvailableManagers = ManagersResult.data;

        RenderAccounts();
        RenderRoleOptions();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
        RenderAccounts();
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

function RenderAccounts() {
    if (!State.Accounts.length) {
        AccountTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-secondary py-4">Chưa có tài khoản nhân viên.</td>
            </tr>
        `;
        return;
    }

    AccountTableBody.innerHTML = State.Accounts.map(Account => `
        <tr>
            <td class="ps-4 fw-semibold">${Account.id}</td>
            <td>
                <div class="fw-medium">${EscapeHtml(Account.managerName)}</div>
                <div class="text-secondary small">ID hồ sơ: ${Account.managerId}</div>
            </td>
            <td>${EscapeHtml(Account.username)}</td>
            <td>${EscapeHtml(Account.email)}</td>
            <td>${RenderRoleBadges(Account.roleLabels)}</td>
            <td>
                <span class="status-dot ${Account.active ? 'bg-success' : 'bg-secondary'}"></span>
                ${Account.active ? 'Đang hoạt động' : 'Tạm khóa'}
            </td>
            <td class="text-end pe-4">
                <button class="btn btn-outline-primary btn-sm icon-btn me-1" type="button" title="Sửa"
                        onclick="OpenEditModal(${Account.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm icon-btn" type="button" title="Xóa"
                        onclick="DeleteAccount(${Account.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function RenderRoleOptions(SelectedRoleIds = []) {
    const SelectedValues = new Set((SelectedRoleIds || []).map(RoleId => String(RoleId)));
    RoleSelect.innerHTML = State.Roles.map(Role => `
        <option value="${Role.id}" ${SelectedValues.has(String(Role.id)) ? 'selected' : ''}>${EscapeHtml(Role.label)}</option>
    `).join('');
}

function RenderAvailableManagerOptions() {
    ManagerSelect.innerHTML = '<option value="">Chọn hồ sơ nhân viên...</option>' + State.AvailableManagers.map(Manager => `
        <option value="${Manager.id}">${EscapeHtml(Manager.label)}</option>
    `).join('');
}

function OpenCreateModal() {
    State.EditingId = null;
    AccountForm.reset();
    document.getElementById('AccountId').value = '';
    document.getElementById('AccountModalTitle').textContent = 'Thêm tài khoản nhân viên';
    document.getElementById('Password').required = true;
    document.getElementById('PasswordHelp').textContent = 'Mật khẩu tối thiểu 6 ký tự.';
    RenderAvailableManagerOptions();
    RenderRoleOptions();
    ManagerSelect.disabled = State.AvailableManagers.length === 0;
    document.getElementById('NoManagerNotice').classList.toggle('d-none', State.AvailableManagers.length !== 0);
    SyncCreateFieldState();
    AccountModal.show();
}

function OpenEditModal(Id) {
    const Account = State.Accounts.find(Item => Item.id === Id);
    if (!Account) {
        ShowAlert('Không tìm thấy tài khoản cần sửa.', 'danger');
        return;
    }

    State.EditingId = Id;
    AccountForm.reset();
    document.getElementById('AccountId').value = Id;
    document.getElementById('AccountModalTitle').textContent = 'Sửa tài khoản nhân viên';
    ManagerSelect.innerHTML = `<option value="${Account.managerId}" selected>${Account.managerId} - ${EscapeHtml(Account.managerName)}</option>`;
    ManagerSelect.disabled = true;
    document.getElementById('Username').value = Account.username;
    document.getElementById('Password').value = '';
    document.getElementById('Password').required = false;
    document.getElementById('PasswordHelp').textContent = 'Bỏ trống nếu không đổi mật khẩu.';
    document.getElementById('Email').value = Account.email;
    document.getElementById('Active').checked = Account.active;
    RenderRoleOptions(Account.roleIds);
    document.getElementById('NoManagerNotice').classList.add('d-none');
    CreateFields.forEach(Field => Field.disabled = false);
    document.getElementById('SaveAccountButton').disabled = false;
    AccountModal.show();
}

function SyncCreateFieldState() {
    if (State.EditingId) {
        return;
    }

    const CanCreate = State.AvailableManagers.length > 0 && Boolean(ManagerSelect.value);
    CreateFields.forEach(Field => Field.disabled = !CanCreate);
    document.getElementById('SaveAccountButton').disabled = !CanCreate;
}

async function SaveAccount(Event) {
    Event.preventDefault();

    const IsEditing = Boolean(State.EditingId);
    const Payload = {
        username: document.getElementById('Username').value.trim(),
        password: document.getElementById('Password').value.trim(),
        email: document.getElementById('Email').value.trim(),
        roleIds: [...RoleSelect.selectedOptions]
            .map(Option => Number(Option.value))
            .filter(RoleId => Number.isFinite(RoleId) && RoleId > 0),
        active: document.getElementById('Active').checked
    };

    if (!IsEditing) {
        Payload.managerId = Number(ManagerSelect.value);
    }

    if (IsEditing && !Payload.password) {
        delete Payload.password;
    }

    try {
        const Result = await ApiRequest(IsEditing ? `${ApiUrl}/${State.EditingId}` : ApiUrl, {
            method: IsEditing ? 'PUT' : 'POST',
            body: JSON.stringify(Payload)
        });

        ShowAlert(Result.message, 'success');
        AccountModal.hide();
        await LoadPage();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

async function DeleteAccount(Id) {
    if (!confirm('Xóa tài khoản nhân viên này? Hồ sơ cá nhân vẫn được giữ lại.')) {
        return;
    }

    try {
        const Result = await ApiRequest(`${ApiUrl}/${Id}`, { method: 'DELETE' });
        ShowAlert(Result.message, 'success');
        await LoadPage();
    } catch (Error) {
        ShowAlert(Error.message, 'danger');
    }
}

function ShowAlert(Message, Type) {
    AlertBox.className = `alert alert-${Type}`;
    AlertBox.textContent = Message;
}

function RenderRoleBadges(RoleLabels) {
    const Labels = RoleLabels || [];
    if (!Labels.length) {
        return '<span class="badge text-bg-secondary">Chưa có vai trò</span>';
    }

    return Labels
        .map(Label => `<span class="badge text-bg-light border me-1 mb-1">${EscapeHtml(Label)}</span>`)
        .join('');
}

function EscapeHtml(Value) {
    return String(Value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
