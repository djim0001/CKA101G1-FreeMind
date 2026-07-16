document.addEventListener('DOMContentLoaded', () => {
  const toast = document.querySelector('.toast') || Object.assign(document.body.appendChild(document.createElement('div')), { className: 'toast' });
  let toastTimer;
  const notify = message => {
    if (!message) return;
    toast.textContent = message;
    toast.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toast.classList.remove('show'), 2300);
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

  document.querySelectorAll('input[name="returnUrl"]').forEach(input => {
    input.value = window.location.pathname + window.location.search;
  });

  document.querySelectorAll('[data-tabs]').forEach(tabSet => {
    tabSet.querySelectorAll('[data-tab]').forEach(button => button.addEventListener('click', () => {
      tabSet.querySelectorAll('[data-tab]').forEach(item => item.classList.remove('active'));
      button.classList.add('active');
      tabSet.parentElement.querySelectorAll('[data-panel]').forEach(panel => {
        panel.hidden = panel.dataset.panel !== button.dataset.tab;
      });
    }));
  });

  document.querySelectorAll('[data-confirm-form]').forEach(form => form.addEventListener('submit', event => {
    if (!window.confirm(form.dataset.confirmForm)) event.preventDefault();
  }));

  document.querySelectorAll('[data-close-modal]').forEach(button => button.addEventListener('click', () => {
    const modal = button.closest('.modal-shell');
    if (modal) modal.hidden = true;
  }));

  document.querySelectorAll('.modal-shell').forEach(modal => modal.addEventListener('click', event => {
    if (event.target === modal) modal.hidden = true;
  }));

  document.querySelectorAll('.modal-shell[data-open-on-load="true"]').forEach(modal => {
    modal.hidden = false;
  });

  const search = document.querySelector('[data-course-search]');
  const topic = document.querySelector('[data-topic-filter]');
  const filterCourses = () => {
    const term = (search?.value || '').trim().toLowerCase();
    const category = topic?.value || '';
    document.querySelectorAll('[data-course-card]').forEach(card => {
      const matchesText = card.textContent.toLowerCase().includes(term);
      const matchesCategory = !category || card.dataset.category === category;
      card.hidden = !(matchesText && matchesCategory);
    });
  };
  search?.addEventListener('input', filterCourses);
  topic?.addEventListener('change', filterCourses);

  document.querySelectorAll('[data-toggle-target]').forEach(button => button.addEventListener('click', () => {
    const target = document.querySelector(button.dataset.toggleTarget);
    if (!target) return;
    target.hidden = !target.hidden;
    if (!target.hidden) target.querySelector('input,select,textarea')?.focus();
  }));

  document.querySelectorAll('[data-review-rating]').forEach(select => select.addEventListener('change', () => {
    const preview = document.querySelector('[data-rating-preview]');
    if (preview) preview.textContent = '★'.repeat(Number(select.value) || 0) + '☆'.repeat(5 - (Number(select.value) || 0));
  }));

  const discount = document.querySelector('[data-order-discount]');
  const total = document.querySelector('[data-order-total]');
  if (discount && total) {
    const original = Number(total.dataset.original || 0);
    total.textContent = `NT$ ${Math.max(0, original - Number(discount.dataset.amount || 0)).toLocaleString('zh-TW')}`;
  }

  const courseVideo = document.querySelector('#courseVideo[data-course-order-id]');
  if (courseVideo) {
    const savedSeconds = Number(courseVideo.dataset.playbackSeconds || 0);
    const courseOrderId = Number(courseVideo.dataset.courseOrderId);
    const courseId = Number(courseVideo.dataset.courseId);
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    let lastSavedSecond = -1;

    const savePlaybackPosition = seconds => {
      if (!Number.isFinite(seconds) || seconds < 0 || !courseOrderId || !courseId) return;
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
      fetch('/member/course/playback-position', {
        method: 'POST',
        headers,
        body: JSON.stringify({ courseOrderId, courseId, playbackSeconds: seconds })
      }).catch(error => console.error('儲存播放位置失敗', error));
    };

    courseVideo.addEventListener('loadedmetadata', () => {
      if (savedSeconds > 0 && savedSeconds < courseVideo.duration) courseVideo.currentTime = savedSeconds;
    });
    courseVideo.addEventListener('timeupdate', () => {
      const currentSecond = Math.floor(courseVideo.currentTime);
      if (currentSecond > 0 && currentSecond % 10 === 0 && currentSecond !== lastSavedSecond) {
        lastSavedSecond = currentSecond;
        savePlaybackPosition(currentSecond);
      }
    });
    courseVideo.addEventListener('pause', () => savePlaybackPosition(Math.floor(courseVideo.currentTime)));
    courseVideo.addEventListener('ended', () => savePlaybackPosition(Math.floor(courseVideo.duration)));
  }
});
