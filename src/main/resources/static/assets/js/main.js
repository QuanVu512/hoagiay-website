const categoriesContainer = document.querySelector("#categories");
const productList = document.querySelector("#product-list");
const articleList = document.querySelector("#article-list");
const contactForm = document.querySelector("#contact-form");
const formMessage = document.querySelector("#form-message");
const guestActions = document.querySelector("#GuestActions");
const userActions = document.querySelector("#UserActions");
const sessionUsername = document.querySelector("#SessionUsername");
const adminLink = document.querySelector("#AdminLink");

async function fetchJson(url, options) {
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
        const message = data?.message || "Không thể tải dữ liệu";
        throw new Error(message);
    }

    return data;
}

async function loadSession() {
    try {
        const response = await fetchJson("/api/auth/account");
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

function renderEmpty(container, message) {
    container.innerHTML = `<p class="empty">${message}</p>`;
}

function renderCategories(categories) {
    if (!categories.length) {
        renderEmpty(categoriesContainer, "Chưa có danh mục.");
        return;
    }

    categoriesContainer.innerHTML = categories
        .map((category) => `<span class="chip">${category.name}</span>`)
        .join("");
}

function renderProducts(products) {
    if (!products.length) {
        renderEmpty(productList, "Chưa có sản phẩm.");
        return;
    }

    productList.innerHTML = products
        .map((product) => `
            <div class="col">
                <article class="card h-100">
                    <img src="${product.thumbnailUrl || "/assets/images/product-placeholder.svg"}" alt="${EscapeHtml(product.name)}">
                    <div class="card-body">
                        <h3>${EscapeHtml(product.name)}</h3>
                        <p>${EscapeHtml(product.shortDescription || "")}</p>
                        <p class="meta">${EscapeHtml(product.priceLabel || "Liên hệ")}</p>
                    </div>
                </article>
            </div>
        `)
        .join("");
}

function renderArticles(articles) {
    if (!articles.length) {
        renderEmpty(articleList, "Chưa có bài viết.");
        return;
    }

    articleList.innerHTML = articles
        .map((article) => `
            <div class="col">
                <article class="card h-100">
                    <img src="${article.thumbnailUrl || "/assets/images/article-placeholder.svg"}" alt="${EscapeHtml(article.title)}">
                    <div class="card-body">
                        <h3>${EscapeHtml(article.title)}</h3>
                        <p>${EscapeHtml(article.summary || "")}</p>
                    </div>
                </article>
            </div>
        `)
        .join("");
}

async function loadHomeData() {
    const [categories, products, articles] = await Promise.all([
        fetchJson("/api/categories"),
        fetchJson("/api/products?featured=true&page=0&size=4&sort=id,desc"),
        fetchJson("/api/articles?page=0&size=2&sort=publishedAt,desc&sort=id,desc")
    ]);

    renderCategories(categories);
    renderProducts(products.content || []);
    renderArticles(articles.content || []);
}

contactForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    formMessage.textContent = "Đang gửi thông tin...";

    const formData = new FormData(contactForm);
    const payload = Object.fromEntries(formData.entries());

    try {
        const response = await fetchJson("/api/contact-messages", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        contactForm.reset();
        formMessage.textContent = response.message;
    } catch (error) {
        formMessage.textContent = error.message;
    }
});

loadHomeData().catch((error) => {
    renderEmpty(categoriesContainer, error.message);
    renderEmpty(productList, error.message);
    renderEmpty(articleList, error.message);
});

loadSession();
