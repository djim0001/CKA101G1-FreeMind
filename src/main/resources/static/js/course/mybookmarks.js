const emptyState = document.querySelector("#emptyState");
const searchInput = document.querySelector("#courseSearch");
const visibleCount = document.querySelector("#visibleCount");
const tabs = document.querySelectorAll(".tabs button[data-filter]");
const paginationWrap = document.querySelector("#paginationWrap");
const pageSize = Number(paginationWrap?.dataset.pageSize || 5);
const serverPaginationHtml = paginationWrap?.innerHTML || "";

let activeFilter = "all";
let currentPage = 1;
let matchedCards = [];

document.querySelectorAll('input[name="returnUrl"]').forEach((input) => {
  input.value = window.location.pathname + window.location.search;
});

function getCourseCards() {
  return [...document.querySelectorAll(".course-card")];
}

function getMatchedCards() {
  const keyword = searchInput?.value.trim().toLowerCase() || "";

  return getCourseCards().filter((card) => {
    const text = card.textContent.toLowerCase();
    const rating = Number(card.dataset.rating || 0);
    const saveCount = Number(card.dataset.saveCount || 0);
    const matchKeyword = !keyword || text.includes(keyword);
    const matchFilter =
      activeFilter === "all" ||
      (activeFilter === "high-rating" && rating >= 4) ||
      (activeFilter === "popular" && saveCount >= 50);
    return matchKeyword && matchFilter;
  });
}

function isClientPagingActive() {
  const keyword = searchInput?.value.trim() || "";
  return keyword !== "" || activeFilter !== "all";
}

function updateCards() {
  const start = (currentPage - 1) * pageSize;
  const end = start + pageSize;
  const pageCards = matchedCards.slice(start, end);

  getCourseCards().forEach((card) => {
    card.classList.toggle("is-hidden", !pageCards.includes(card));
  });
}

function renderPagination() {
  if (!paginationWrap) return;

  const totalPages = Math.ceil(matchedCards.length / pageSize);

  if (totalPages <= 1) {
    paginationWrap.classList.add("is-hidden");
    paginationWrap.innerHTML = "";
    return;
  }

  paginationWrap.classList.remove("is-hidden");

  const buttons = [];
  buttons.push(`
    <button type="button" class="page-link ${currentPage === 1 ? "disabled" : ""}" data-page="${currentPage - 1}" ${currentPage === 1 ? "disabled" : ""}>
      上一頁
    </button>
  `);

  for (let page = 1; page <= totalPages; page += 1) {
    buttons.push(`
      <button type="button" class="page-link ${page === currentPage ? "active" : ""}" data-page="${page}" ${page === currentPage ? 'aria-current="page"' : ""}>
        ${page}
      </button>
    `);
  }

  buttons.push(`
    <button type="button" class="page-link ${currentPage === totalPages ? "disabled" : ""}" data-page="${currentPage + 1}" ${currentPage === totalPages ? "disabled" : ""}>
      下一頁
    </button>
  `);

  paginationWrap.innerHTML = buttons.join("");
}

function applyFilters() {
  if (!isClientPagingActive()) {
    const cards = getCourseCards();

    cards.forEach((card) => card.classList.remove("is-hidden"));

    if (visibleCount) {
      visibleCount.textContent = cards.length;
    }

    if (emptyState) {
      emptyState.classList.toggle("is-hidden", cards.length !== 0);
    }

    if (paginationWrap) {
      paginationWrap.innerHTML = serverPaginationHtml;
      paginationWrap.classList.toggle("is-hidden", serverPaginationHtml.trim() === "");
    }

    return;
  }

  matchedCards = getMatchedCards();
  const totalPages = Math.max(1, Math.ceil(matchedCards.length / pageSize));

  if (currentPage > totalPages) {
    currentPage = totalPages;
  }

  if (visibleCount) {
    visibleCount.textContent = matchedCards.length;
  }

  if (emptyState) {
    emptyState.classList.toggle("is-hidden", matchedCards.length !== 0);
  }

  updateCards();
  renderPagination();
}

tabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    tabs.forEach((item) => item.classList.remove("active"));
    tab.classList.add("active");
    activeFilter = tab.dataset.filter;
    currentPage = 1;
    applyFilters();
  });
});

searchInput?.addEventListener("input", () => {
  currentPage = 1;
  applyFilters();
});

paginationWrap?.addEventListener("click", (event) => {
  const button = event.target.closest("[data-page]");
  if (!button || button.disabled) return;

  currentPage = Number(button.dataset.page);
  applyFilters();
  document.querySelector("#courseList")?.scrollIntoView({ behavior: "smooth", block: "start" });
});

function closeCartModal() {
  const modal = document.querySelector("#cartModal");
  if (modal) {
    modal.classList.add("is-hidden");
  }
}

applyFilters();
