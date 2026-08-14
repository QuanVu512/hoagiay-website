function BuildAdminPageUrl(BaseUrl, State) {
    const Url = new URL(BaseUrl, window.location.origin);
    Url.searchParams.set('page', State.Page ?? 0);
    Url.searchParams.set('size', State.Size ?? 10);
    GetAdminSortValues(State.Sort).forEach(SortValue => Url.searchParams.append('sort', SortValue));
    Object.entries(State.Filters || {}).forEach(([Key, Value]) => {
        if (Value !== null && Value !== undefined && String(Value).trim() !== '') {
            Url.searchParams.set(Key, String(Value).trim());
        }
    });
    return Url.pathname + Url.search;
}

function ApplyAdminPage(State, ItemsKey, PageData) {
    const SafePageData = PageData || {};
    State[ItemsKey] = SafePageData.content || [];
    State.Page = SafePageData.page ?? State.Page ?? 0;
    State.Size = SafePageData.size ?? State.Size ?? 10;
    State.PageData = SafePageData;
}

function BindAdminListControls(SortSelectId, SizeSelectId, State, Reload) {
    const SortSelect = document.getElementById(SortSelectId);
    if (SortSelect) {
        SortSelect.value = State.Sort || SortSelect.value;
        SortSelect.addEventListener('change', () => {
            State.Sort = SortSelect.value;
            State.Page = 0;
            Reload();
        });
    }

    const SizeSelect = document.getElementById(SizeSelectId);
    if (SizeSelect) {
        SizeSelect.value = String(State.Size ?? 10);
        SizeSelect.addEventListener('change', () => {
            State.Size = Number(SizeSelect.value || 10);
            State.Page = 0;
            Reload();
        });
    }
}

function BindAdminFilterControls(Config, State, Reload) {
    const SearchInput = document.getElementById(Config.searchId);
    const ApplyButton = document.getElementById(Config.applyButtonId);
    const ResetButton = document.getElementById(Config.resetButtonId);
    const Filters = Config.filters || [];

    const ApplyFilters = () => {
        State.Filters = {};
        if (SearchInput && SearchInput.value.trim()) {
            State.Filters.keyword = SearchInput.value.trim();
        }
        Filters.forEach(Filter => {
            const Element = document.getElementById(Filter.id);
            if (Element && Element.value !== '') {
                State.Filters[Filter.key] = Element.value;
            }
        });
        State.Page = 0;
        Reload();
    };

    if (ApplyButton) {
        ApplyButton.addEventListener('click', ApplyFilters);
    }
    if (SearchInput) {
        SearchInput.addEventListener('keydown', Event => {
            if (Event.key === 'Enter') {
                Event.preventDefault();
                ApplyFilters();
            }
        });
    }
    Filters.forEach(Filter => {
        const Element = document.getElementById(Filter.id);
        if (Element) {
            Element.addEventListener('change', ApplyFilters);
        }
    });
    if (ResetButton) {
        ResetButton.addEventListener('click', () => {
            if (SearchInput) {
                SearchInput.value = '';
            }
            Filters.forEach(Filter => {
                const Element = document.getElementById(Filter.id);
                if (Element) {
                    Element.value = '';
                }
            });
            State.Filters = {};
            State.Page = 0;
            Reload();
        });
    }
}

function RenderAdminPagination(ContainerId, PageData, OnPageChange) {
    const Container = document.getElementById(ContainerId);
    if (!Container) {
        return;
    }

    const TotalElements = PageData?.totalElements ?? 0;
    if (!TotalElements) {
        Container.innerHTML = '<span class="text-secondary small">Không có dữ liệu để phân trang.</span>';
        return;
    }

    const CurrentPage = PageData.page ?? 0;
    const Size = PageData.size ?? 10;
    const TotalPages = PageData.totalPages || 1;
    const Start = CurrentPage * Size + 1;
    const End = Start + (PageData.numberOfElements ?? 0) - 1;

    Container.innerHTML = `
        <div class="text-secondary small">
            Hiển thị ${Start}-${End} / ${TotalElements} mục
        </div>
        <div class="btn-group btn-group-sm" role="group" aria-label="Phân trang">
            <button class="btn btn-outline-secondary" type="button" data-admin-page="${CurrentPage - 1}" ${PageData.first ? 'disabled' : ''}>
                Trước
            </button>
            <button class="btn btn-outline-secondary disabled" type="button">
                Trang ${CurrentPage + 1} / ${TotalPages}
            </button>
            <button class="btn btn-outline-secondary" type="button" data-admin-page="${CurrentPage + 1}" ${PageData.last ? 'disabled' : ''}>
                Sau
            </button>
        </div>
    `;

    Container.querySelectorAll('[data-admin-page]').forEach(Button => {
        Button.addEventListener('click', () => OnPageChange(Number(Button.dataset.adminPage)));
    });
}

function GetAdminSortValues(SortValue) {
    if (!SortValue) {
        return [];
    }

    return String(SortValue)
        .split(';')
        .map(Value => Value.trim())
        .filter(Boolean);
}
