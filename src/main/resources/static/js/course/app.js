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
