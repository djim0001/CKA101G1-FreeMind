/* 共用互動腳本：抽自 js/course/member-js.js 的通用段（data-attribute 驅動）。
   注意：勿與 course 的 member-js.js / admin-js.js 同頁載入（handler 相同會雙重綁定）。 */
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

  // 頁面載入時若帶有一次性訊息（如修改成功），以 toast 呈現
  const flash = document.querySelector('[data-flash-message]');
  if (flash) notify(flash.dataset.flashMessage || flash.textContent.trim());

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
      if (tabSet.hasAttribute('data-sync-tab-url')) {
        const url = new URL(window.location.href);
        url.searchParams.set('tab', button.dataset.tab);
        window.history.replaceState(null, '', url);
      }
    }));
  });

  document.querySelectorAll('[data-preserve-tab]').forEach(container => {
    const tab = container.dataset.preserveTab;
    container.querySelectorAll('a[href]').forEach(link => {
      const url = new URL(link.href, window.location.href);
      url.searchParams.set('tab', tab);
      link.href = url;
    });
  });

  const requestedTab = new URLSearchParams(window.location.search).get('tab');
  if (requestedTab) {
    [...document.querySelectorAll('[data-tab]')]
      .find(button => button.dataset.tab === requestedTab)
      ?.click();
  }

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

  document.querySelectorAll('[data-toggle-target]').forEach(button => button.addEventListener('click', () => {
    const target = document.querySelector(button.dataset.toggleTarget);
    if (!target) return;
    target.hidden = !target.hidden;
    if (!target.hidden) target.querySelector('input,select,textarea')?.focus();
  }));

  // 圖片上傳前預覽：<input type="file" data-pic-preview="#imgSelector">
  document.querySelectorAll('input[type="file"][data-pic-preview]').forEach(input => {
    input.addEventListener('change', () => {
      const img = document.querySelector(input.dataset.picPreview);
      const file = input.files[0];
      if (!img || !file) return;
      const reader = new FileReader();
      reader.onload = e => {
        img.src = e.target.result;
        img.hidden = false;
        img.style.display = '';
      };
      reader.readAsDataURL(file);
    });
  });

  // 下拉菜單：<button type="button" data-toggle-menu="menuId">...</button> + <div id="menuId" class="profile-menu">...</div>
  document.querySelectorAll('[data-toggle-menu]').forEach(button => {
    const menuId = button.dataset.toggleMenu;
    const menu = document.getElementById(menuId);
    if (!menu) return;

    button.addEventListener('click', e => {
      e.stopPropagation();
      menu.toggleAttribute('data-open');
    });

    menu.addEventListener('click', e => {
      if (e.target.classList.contains('menu-item') && e.target.tagName === 'A') {
        menu.removeAttribute('data-open');
      }
    });
  });

  // 點擊頁面其他地方時關閉所有菜單
  document.addEventListener('click', () => {
    document.querySelectorAll('.profile-menu[data-open]').forEach(menu => {
      menu.removeAttribute('data-open');
    });
  });
});
