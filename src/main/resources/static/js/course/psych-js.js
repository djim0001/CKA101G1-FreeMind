document.addEventListener('DOMContentLoaded', () => {
  const toast = document.querySelector('.toast') || Object.assign(document.body.appendChild(document.createElement('div')), { className: 'toast' });
  let timer;
  const notify = message => {
    toast.textContent = message;
    toast.classList.add('show');
    clearTimeout(timer);
    timer = setTimeout(() => toast.classList.remove('show'), 2300);
  };

  document.querySelectorAll('[data-toast]').forEach(element => element.addEventListener('click', () => notify(element.dataset.toast)));
  const enhancePagination = () => {
    const roots = new Set();
    document.querySelectorAll('main a[href*="page="], main a[href*="currentPage="]').forEach(link => {
      roots.add(link.closest('nav, ul, ol, .pagination') || link.parentElement);
    });
    document.querySelectorAll('main div').forEach(element => {
      const ownText = [...element.childNodes].filter(node => node.nodeType === Node.TEXT_NODE).map(node => node.textContent).join(' ');
      if (/第\s*\d+\s*\/\s*\d+\s*頁/.test(ownText)) roots.add(element);
    });
    roots.forEach(root => {
      if (!root) return;
      root.classList.add('pagination-ui');
      root.setAttribute('role', 'navigation');
      root.setAttribute('aria-label', '分頁導覽');
      root.querySelectorAll('a').forEach(link => {
        const label = link.textContent.trim();
        const page = new URL(link.href, window.location.href).searchParams.get('page');
        if (/最前|第一/.test(label)) link.setAttribute('aria-label', '前往第一頁');
        else if (/上一/.test(label)) link.setAttribute('aria-label', '前往上一頁');
        else if (/下一/.test(label)) link.setAttribute('aria-label', '前往下一頁');
        else if (/最後|最末/.test(label)) link.setAttribute('aria-label', '前往最後一頁');
        else if (page) link.setAttribute('aria-label', `前往第 ${page} 頁`);
        if (link.matches('.active, [aria-current="page"]') || link.parentElement?.classList.contains('active')) link.setAttribute('aria-current', 'page');
      });
    });
  };
  enhancePagination();
  document.querySelectorAll('[data-tabs]').forEach(tabSet => tabSet.querySelectorAll('[data-tab]').forEach(button => button.addEventListener('click', () => {
    tabSet.querySelectorAll('[data-tab]').forEach(item => item.classList.remove('active'));
    button.classList.add('active');
    tabSet.parentElement.querySelectorAll('[data-panel]').forEach(panel => panel.hidden = panel.dataset.panel !== button.dataset.tab);
  })));

  document.querySelectorAll('[data-video-upload]').forEach(input => input.addEventListener('change', () => {
    const target = document.querySelector(`[data-video-preview="${input.dataset.videoUpload}"]`);
    const fileName = document.querySelector(`[data-video-name="${input.dataset.videoUpload}"]`);
    const file = input.files[0];
    if (!file || !target) return;
    if (target.dataset.objectUrl) URL.revokeObjectURL(target.dataset.objectUrl);
    target.dataset.objectUrl = URL.createObjectURL(file);
    target.src = target.dataset.objectUrl;
    target.hidden = false;
    if (fileName) fileName.textContent = file.name;
  }));

  document.querySelectorAll('[data-confirm-form]').forEach(form => form.addEventListener('submit', event => {
    if (!window.confirm(form.dataset.confirmForm)) event.preventDefault();
  }));

  const modal = document.querySelector('#psychDiscountModal');
  document.querySelectorAll('[data-open-discount]').forEach(button => button.addEventListener('click', () => modal?.removeAttribute('hidden')));
  document.querySelectorAll('[data-close-discount]').forEach(button => button.addEventListener('click', () => modal?.setAttribute('hidden', '')));

  const discountInput = document.querySelector('[data-psych-discount-input]');
  document.querySelectorAll('[data-discount-rate]').forEach(button => button.addEventListener('click', () => {
    document.querySelectorAll('[data-discount-rate]').forEach(item => item.classList.remove('active'));
    button.classList.add('active');
    if (discountInput) discountInput.value = button.dataset.discountRate;
  }));

  const start = document.querySelector('[data-discount-start]');
  if (start) {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const local = new Date(tomorrow.getTime() - tomorrow.getTimezoneOffset() * 60000).toISOString().slice(0,10);
    start.min = local;
  }

  document.querySelectorAll('[data-preview-tab]').forEach(button => button.addEventListener('click', () => {
    document.querySelectorAll('[data-preview-tab]').forEach(item => item.classList.remove('active'));
    button.classList.add('active');
    document.querySelectorAll('[data-preview-panel]').forEach(panel => panel.hidden = panel.dataset.previewPanel !== button.dataset.previewTab);
  }));

  document.querySelectorAll('input[name="returnUrl"]').forEach(input => {
    input.value = window.location.pathname + window.location.search;
  });
});
