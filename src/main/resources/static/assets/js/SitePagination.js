async function FetchSiteJson(url, options) {
    const response = await fetch(url, {
        ...(options || {}),
        credentials: "same-origin",
        headers: {
            ...BuildAuthHeader(),
            ...(options?.headers || {})
        }
    });
    const data = await response.json().catch(() => null);

    if (!response.ok) {
        throw new Error(data?.message || "Không thể tải dữ liệu");
    }

    return data;
}

async function LoadSiteSession() {
    const guestActions = document.querySelector("#GuestActions");
    const userActions = document.querySelector("#UserActions");
    const sessionUsername = document.querySelector("#SessionUsername");
    const adminLink = document.querySelector("#AdminLink");
    if (!guestActions || !userActions) {
        return;
    }

    try {
        const response = await FetchSiteJson("/api/auth/account");
        const account = response.data;

        guestActions.classList.add("d-none");
        userActions.classList.remove("d-none");
        sessionUsername.textContent = account.username || "";
        adminLink.href = account.managementPath || "/admin/account";
        adminLink.classList.toggle("d-none", !account.managementPath);
    } catch (error) {
        ClearAuthTokens();
        guestActions.classList.remove("d-none");
        userActions.classList.add("d-none");
    }
}

function BuildSitePageUrl(baseUrl, state, extraParams = {}) {
    const url = new URL(baseUrl, window.location.origin);
    Object.entries(extraParams).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== "") {
            url.searchParams.set(key, value);
        }
    });
    url.searchParams.set("page", state.Page ?? 0);
    url.searchParams.set("size", state.Size ?? 8);
    GetSiteSortValues(state.Sort).forEach(sortValue => url.searchParams.append("sort", sortValue));
    return url.pathname + url.search;
}

function RenderSitePagination(containerId, pageData, onPageChange) {
    const container = document.querySelector(containerId);
    if (!container) {
        return;
    }

    const totalElements = pageData?.totalElements ?? 0;
    if (!totalElements) {
        container.innerHTML = "";
        return;
    }

    const currentPage = pageData.page ?? 0;
    const totalPages = pageData.totalPages || 1;

    container.innerHTML = `
        <button class="btn btn-outline-danger px-4" type="button" data-site-page="${currentPage - 1}" ${pageData.first ? "disabled" : ""}>
            Trước
        </button>
        <span class="text-secondary fw-medium">Trang ${currentPage + 1} / ${totalPages}</span>
        <button class="btn btn-outline-danger px-4" type="button" data-site-page="${currentPage + 1}" ${pageData.last ? "disabled" : ""}>
            Sau
        </button>
    `;

    container.querySelectorAll("[data-site-page]").forEach(button => {
        button.addEventListener("click", () => onPageChange(Number(button.dataset.sitePage)));
    });
}

function RenderSiteEmpty(container, message) {
    container.innerHTML = `<p class="empty">${message}</p>`;
}

function EscapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}

function GetSiteSortValues(sortValue) {
    if (!sortValue) {
        return [];
    }

    return String(sortValue)
        .split(";")
        .map(value => value.trim())
        .filter(Boolean);
}
