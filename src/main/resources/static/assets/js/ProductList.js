const ProductList = document.querySelector("#ProductList");
const ProductPagination = "#ProductPagination";
const ProductSort = document.querySelector("#ProductSort");
const ProductSize = document.querySelector("#ProductSize");
const FeaturedOnly = document.querySelector("#FeaturedOnly");

const ProductState = {
    Page: 0,
    Size: 8,
    Sort: "id,desc",
    Featured: false
};

ProductSort.addEventListener("change", () => {
    ProductState.Sort = ProductSort.value;
    ProductState.Page = 0;
    LoadProducts();
});

ProductSize.addEventListener("change", () => {
    ProductState.Size = Number(ProductSize.value || 8);
    ProductState.Page = 0;
    LoadProducts();
});

FeaturedOnly.addEventListener("change", () => {
    ProductState.Featured = FeaturedOnly.checked;
    ProductState.Page = 0;
    LoadProducts();
});

async function LoadProducts() {
    try {
        const pageData = await FetchSiteJson(BuildSitePageUrl("/api/products", ProductState, {
            featured: ProductState.Featured ? "true" : ""
        }));
        RenderProducts(pageData.content || []);
        RenderSitePagination(ProductPagination, pageData, page => {
            ProductState.Page = page;
            LoadProducts();
        });
    } catch (error) {
        RenderSiteEmpty(ProductList, error.message);
    }
}

function RenderProducts(products) {
    if (!products.length) {
        RenderSiteEmpty(ProductList, "Chưa có sản phẩm phù hợp.");
        return;
    }

    ProductList.innerHTML = products
        .map(product => `
            <div class="col">
                <article class="card h-100">
                    <img src="${product.thumbnailUrl || "/assets/images/product-placeholder.svg"}" alt="${EscapeHtml(product.name)}">
                    <div class="card-body">
                        <h3>${EscapeHtml(product.name)}</h3>
                        <p>${EscapeHtml(product.shortDescription || "")}</p>
                        <p class="meta mb-0">${EscapeHtml(product.priceLabel || "Liên hệ")}</p>
                    </div>
                </article>
            </div>
        `)
        .join("");
}

LoadSiteSession();
LoadProducts();
