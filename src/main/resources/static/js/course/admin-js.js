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

  document.querySelectorAll('[data-order-search-pagination]').forEach(container => {
    const filters = {
      keyword: container.dataset.keyword,
      paymentStatus: container.dataset.paymentStatus,
      orderedDate: container.dataset.orderedDate
    };
    container.querySelectorAll('a[href]').forEach(link => {
      const url = new URL(link.href, window.location.href);
      Object.entries(filters).forEach(([name, value]) => {
        if (value !== undefined && value !== '') url.searchParams.set(name, value);
        else url.searchParams.delete(name);
      });
      link.href = url;
    });
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

  const requestedTab = new URLSearchParams(window.location.search).get('tab');
  const initialTab = document.querySelector('[data-refund-detail]') ? 'refunds' : requestedTab;
  if (initialTab) {
    [...document.querySelectorAll('[data-tab]')]
      .find(button => button.dataset.tab === initialTab)
      ?.click();
  }

  document.querySelectorAll('[data-confirm-form]').forEach(form => form.addEventListener('submit', event => {
    if (!window.confirm(form.dataset.confirmForm)) event.preventDefault();
  }));

  const audience = document.querySelector('[data-audience-select]');
  const audienceThreshold = document.querySelector('[data-audience-threshold]');
  const audienceThresholdInput = document.querySelector('[data-audience-threshold-input]');
  const updateAudience = () => {
    if (!audience) return;
    const needsThreshold = audience.value === 'spending';
    audienceThreshold.hidden = !needsThreshold;
    audienceThresholdInput.required = needsThreshold;
    audienceThresholdInput.disabled = !needsThreshold;
  };
  audience?.addEventListener('change', updateAudience);
  updateAudience();

  const discount = document.querySelector('[data-admin-discount]');
  const limit = document.querySelector('[data-admin-limit]');
  const threshold = document.querySelector('[data-admin-threshold]');
  const example = document.querySelector('[data-discount-example]');
  const updateDiscountExample = () => {
    if (!discount || !limit || !threshold || !example) return;
    const order = Math.max(Number(threshold.value) || 0, 1680);
    const raw = Math.round(order * (1 - Number(discount.value)));
    const actual = Math.min(raw, Number(limit.value) || 0);
    example.textContent = `示例訂單 NT$ ${order.toLocaleString('zh-TW')}，原始折抵 NT$ ${raw.toLocaleString('zh-TW')}；實際折抵 NT$ ${actual.toLocaleString('zh-TW')}。`;
  };
  [discount, limit, threshold].forEach(input => input?.addEventListener('input', updateDiscountExample));
  updateDiscountExample();

  const couponSpecForm = document.querySelector('[data-coupon-spec-form]');
  const couponTemplate = document.querySelector('[data-coupon-template]');
  const newCouponButton = document.querySelector('[data-new-coupon]');
  const couponName = document.querySelector('#couponName');
  const discountDuration = document.querySelector('#discountDuration');
  const couponSpecInputs = [couponName, discount, limit, threshold, discountDuration].filter(Boolean);

  couponTemplate?.addEventListener('change', () => {
    const selectedCoupon = couponTemplate.selectedOptions[0];
    if (!selectedCoupon?.value || !couponSpecForm) return;

    couponName.value = selectedCoupon.dataset.couponName || '';
    discount.value = selectedCoupon.dataset.discount || '';
    limit.value = selectedCoupon.dataset.discountLimit || '';
    threshold.value = selectedCoupon.dataset.triggerThreshold || '';
    discountDuration.value = selectedCoupon.dataset.discountDuration || '';
    updateDiscountExample();
    notify(`已載入「${selectedCoupon.dataset.couponName || selectedCoupon.textContent.trim()}」的優惠券規格`);
  });

  newCouponButton?.addEventListener('click', () => {
    if (!couponSpecForm) return;
    couponSpecForm.reset();
    couponSpecInputs.forEach(input => { input.value = ''; });
    if (couponTemplate) couponTemplate.value = '';
    updateDiscountExample();
    requestAnimationFrame(() => couponName?.focus());
    notify('已清空優惠券規格，請建立新的優惠券');
  });

  const dialog = document.querySelector('[data-take-down-dialog]');
  const courseName = document.querySelector('[data-take-down-course]');
  const courseId = document.querySelector('[data-take-down-course-id]');
  const reason = document.querySelector('[data-take-down-reason]');
  let takeDownTrigger = null;

  const closeTakeDownDialog = () => {
    if (dialog?.open) dialog.close();
  };

  document.querySelectorAll('[data-open-take-down]').forEach(button => button.addEventListener('click', () => {
    takeDownTrigger = button;
    if (courseName) courseName.textContent = button.dataset.courseName || '';
    if (courseId) courseId.value = button.dataset.courseId || '';
    if (reason) reason.value = '';
    if (dialog && !dialog.open) {
      dialog.showModal();
      document.body.classList.add('dialog-open');
      requestAnimationFrame(() => reason?.focus());
    }
  }));
  document.querySelectorAll('[data-close-take-down]').forEach(button => button.addEventListener('click', closeTakeDownDialog));

  dialog?.addEventListener('click', event => {
    if (event.target === dialog) closeTakeDownDialog();
  });

  dialog?.addEventListener('close', () => {
    document.body.classList.remove('dialog-open');
    takeDownTrigger?.focus();
  });

  dialog?.addEventListener('cancel', () => {
    document.body.classList.remove('dialog-open');
  });

  dialog?.querySelector('form')?.addEventListener('submit', event => {
    if (!reason?.value) {
      event.preventDefault();
      reason?.focus();
      notify('請先選擇下架原因');
    }
  });

  document.querySelectorAll('[data-close-modal]').forEach(button => button.addEventListener('click', () => {
    const modal = button.closest('.modal, .coupon-modal');
    if (modal) modal.style.display = 'none';
  }));

  document.querySelector('[data-approve-review]')?.addEventListener('click', event => {
    const checks = [...document.querySelectorAll('.check-list input[type="checkbox"]')];
    if (checks.some(check => !check.checked)) {
      event.preventDefault();
      notify('請先完成所有審核檢核項目');
    }
  });
});
