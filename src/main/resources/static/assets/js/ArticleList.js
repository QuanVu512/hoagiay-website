const ArticleList = document.querySelector("#ArticleList");
const ArticlePagination = "#ArticlePagination";
const ArticleSort = document.querySelector("#ArticleSort");
const ArticleSize = document.querySelector("#ArticleSize");

const ArticleState = {
    Page: 0,
    Size: 6,
    Sort: "publishedAt,desc;id,desc"
};

ArticleSort.addEventListener("change", () => {
    ArticleState.Sort = ArticleSort.value;
    ArticleState.Page = 0;
    LoadArticles();
});

ArticleSize.addEventListener("change", () => {
    ArticleState.Size = Number(ArticleSize.value || 6);
    ArticleState.Page = 0;
    LoadArticles();
});

async function LoadArticles() {
    try {
        const pageData = await FetchSiteJson(BuildSitePageUrl("/api/articles", ArticleState));
        RenderArticles(pageData.content || []);
        RenderSitePagination(ArticlePagination, pageData, page => {
            ArticleState.Page = page;
            LoadArticles();
        });
    } catch (error) {
        RenderSiteEmpty(ArticleList, error.message);
    }
}

function RenderArticles(articles) {
    if (!articles.length) {
        RenderSiteEmpty(ArticleList, "Chưa có bài viết.");
        return;
    }

    ArticleList.innerHTML = articles
        .map(article => `
            <div class="col">
                <article class="card h-100">
                    <img src="${article.thumbnailUrl || "/assets/images/article-placeholder.svg"}" alt="${EscapeHtml(article.title)}">
                    <div class="card-body">
                        <h3>${EscapeHtml(article.title)}</h3>
                        <p>${EscapeHtml(article.summary || "")}</p>
                        <p class="text-secondary small mb-0">${FormatArticleDate(article.publishedAt)}</p>
                    </div>
                </article>
            </div>
        `)
        .join("");
}

function FormatArticleDate(value) {
    if (!value) {
        return "";
    }

    return new Intl.DateTimeFormat("vi-VN", {
        dateStyle: "medium"
    }).format(new Date(value));
}

LoadSiteSession();
LoadArticles();
