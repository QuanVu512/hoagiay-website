const categoriesContainer = document.querySelector("#categories");
const productList = document.querySelector("#product-list");
const articleList = document.querySelector("#article-list");
const contactForm = document.querySelector("#contact-form");
const formMessage = document.querySelector("#form-message");
const guestActions = document.querySelector("#GuestActions");
const userActions = document.querySelector("#UserActions");
const sessionUsername = document.querySelector("#SessionUsername");
const adminLink = document.querySelector("#AdminLink");

function readCookie(name) {
    return document.cookie
        .split("; ")
        .find((item) => item.startsWith(`${name}=`))
        ?.split("=")
        .slice(1)
        .join("=") || "";
}

function buildCsrfHeader() {
    const token = readCookie("XSRF-TOKEN");
    return token ? { "X-XSRF-TOKEN": decodeURIComponent(token) } : {};
}

async function fetchJson(url, options) {
    const response = await fetch(url, {
        ...(options || {}),
        headers: {
            ...buildCsrfHeader(),
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
        const response = await fetchJson("/api/session");
        const session = response.data;

        guestActions.classList.toggle("d-none", session.loggedIn);
        userActions.classList.toggle("d-none", !session.loggedIn);
        sessionUsername.textContent = session.username || "";
        adminLink.classList.toggle("d-none", !session.admin);
    } catch (error) {
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
            <article class="card">
                <img src="${product.thumbnailUrl || "/assets/images/product-placeholder.svg"}" alt="${product.name}">
                <div class="card-body">
                    <h3>${product.name}</h3>
                    <p>${product.shortDescription || ""}</p>
                    <p class="meta">${product.priceLabel || "Liên hệ"}</p>
                </div>
            </article>
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
            <article class="card">
                <img src="${article.thumbnailUrl || "/assets/images/article-placeholder.svg"}" alt="${article.title}">
                <div class="card-body">
                    <h3>${article.title}</h3>
                    <p>${article.summary || ""}</p>
                </div>
            </article>
        `)
        .join("");
}

async function loadHomeData() {
    const [categories, products, articles] = await Promise.all([
        fetchJson("/api/categories"),
        fetchJson("/api/products?featured=true"),
        fetchJson("/api/articles")
    ]);

    renderCategories(categories);
    renderProducts(products);
    renderArticles(articles);
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
