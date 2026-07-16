document.addEventListener('DOMContentLoaded', () => {
  const stateSelect = document.querySelector('[data-course-state]');
  if (!stateSelect) return;

  const stateBadge = document.querySelector('[data-state-badge]');
  const editorNotice = document.querySelector('[data-editor-notice]');
  const submitReview = document.querySelector('[data-submit-review]');
  const saveCourse = document.querySelector('[data-save-course]');
  const preReviewSections = [...document.querySelectorAll('[data-pre-review-section]')];
  const discountSection = document.querySelector('[data-discount-section]');
  const discountInputs = [...document.querySelectorAll('[data-discount-input]')];
  const discountStatus = document.querySelector('[data-discount-status]');
  const discountLock = document.querySelector('[data-discount-lock]');
  const saveDiscount = document.querySelector('[data-save-discount]');
  const price = document.querySelector('#price');
  const pricePreview = document.querySelector('#price-preview');
  const discountRate = document.querySelector('#discount-rate');
  const discountPrice = document.querySelector('#discount-price');
  const discountOriginal = document.querySelector('[data-discount-original]');
  const discountSaving = document.querySelector('[data-discount-saving]');
  const discountPeriod = document.querySelector('[data-discount-period]');
  const otherRateField = document.querySelector('[data-other-rate-field]');
  const toggleOtherRate = document.querySelector('[data-toggle-other-rate]');
  const clearDiscount = document.querySelector('[data-clear-discount]');
  const quickRateButtons = [...document.querySelectorAll('[data-quick-rate]')];
  const discountStart = document.querySelector('#discount-start');
  const discountEnd = document.querySelector('#discount-end');
  const courseForm = document.querySelector('[data-course-editor]');
  const videoUploads = [...document.querySelectorAll('[data-video-upload]')];
  const uploadRequirement = document.querySelector('[data-upload-requirement]');

  const states = {
    draft: { label: '草稿', className: 'status warning', notice: '草稿階段可編輯課程資料、純文字內容大綱及原價；折扣須等課程上架後才能設定。', editable: true },
    reviewing: { label: '審核中', className: 'status reviewing', notice: '課程正在審核，基本資料、內容大綱與定價暫時鎖定。', editable: false },
    approved: { label: '審核成功', className: 'status success', notice: '課程已審核成功，等待上架；上架前仍不能設定折扣。', editable: false },
    rejected: { label: '審核失敗', className: 'status danger', notice: '課程審核失敗，可依退回原因修改資料、內容大綱及原價後再次送審。', editable: true },
    published: { label: '已上架', className: 'status success', notice: '課程已上架，現在可以設定一折至九五折的期間折扣，每半折一個級距；折扣最早從隔日起算。', editable: false },
    unpublished: { label: '已下架', className: 'status muted', notice: '課程已下架，所有折扣停止且不可新增折扣。', editable: false }
  };

  const tomorrowAtMidnight = () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(0, 0, 0, 0);
    const local = new Date(tomorrow.getTime() - tomorrow.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 16);
  };

  const formatMoney = value => `NT$ ${Math.max(0, Math.round(Number(value) || 0)).toLocaleString('zh-TW')}`;
  const updateDiscountPrice = () => {
    const originalPrice = Number(price.value) || 0;
    const hasDiscount = Boolean(discountRate.value);
    const rate = hasDiscount ? Number(discountRate.value) : 1;
    const salePrice = originalPrice * rate;
    const saving = originalPrice - salePrice;
    const isPublished = stateSelect.value === 'published';

    pricePreview.value = formatMoney(originalPrice);
    discountOriginal.textContent = formatMoney(originalPrice);
    discountPrice.textContent = formatMoney(salePrice);
    discountSaving.textContent = formatMoney(saving);
    discountPeriod.hidden = !hasDiscount;
    discountStart.required = hasDiscount;
    discountEnd.required = hasDiscount;
    discountStart.disabled = !isPublished || !hasDiscount;
    discountEnd.disabled = !isPublished || !hasDiscount;
    saveDiscount.textContent = hasDiscount ? '儲存折扣設定' : '取消現有折扣';

    quickRateButtons.forEach(button => {
      button.classList.toggle('active', button.dataset.quickRate === discountRate.value);
    });
    clearDiscount.classList.toggle('active', !hasDiscount);
    const isQuickRate = quickRateButtons.some(button => button.dataset.quickRate === discountRate.value);
    toggleOtherRate.classList.toggle('active', !otherRateField.hidden || (hasDiscount && !isQuickRate));
  };

  const formatFileSize = bytes => {
    if (bytes < 1024 * 1024) return `${Math.max(1, Math.round(bytes / 1024))} KB`;
    return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
  };

  const updateUploadRequirement = () => {
    const complete = videoUploads.every(input => input.files.length > 0);
    uploadRequirement.textContent = complete
      ? '影片要件已完成：完整課程影片與課程預覽影片皆已選擇。'
      : '送審條件：完整課程影片與課程預覽影片皆須完成選擇。';
    uploadRequirement.classList.toggle('complete', complete);
    return complete;
  };

  videoUploads.forEach(input => input.addEventListener('change', () => {
    const type = input.dataset.videoUpload;
    const card = document.querySelector(`[data-upload-card="${type}"]`);
    const result = document.querySelector(`[data-upload-result="${type}"]`);
    const file = input.files[0];
    card.classList.remove('upload-error');
    if (!file) {
      card.classList.remove('has-file');
      result.hidden = true;
      updateUploadRequirement();
      return;
    }
    card.classList.add('has-file');
    result.hidden = false;
    result.querySelector('[data-file-name]').textContent = file.name;
    result.querySelector('[data-file-size]').textContent = `${formatFileSize(file.size)}・等待上傳`;
    const player = document.querySelector(`[data-video-player="${type}"]`);
    if (player.dataset.objectUrl) URL.revokeObjectURL(player.dataset.objectUrl);
    player.dataset.objectUrl = URL.createObjectURL(file);
    player.src = player.dataset.objectUrl;
    player.hidden = false;
    window.setTimeout(() => {
      result.querySelector('[data-file-size]').textContent = `${formatFileSize(file.size)}・已準備完成`;
    }, 450);
    updateUploadRequirement();
  }));

  document.querySelectorAll('[data-remove-video]').forEach(button => button.addEventListener('click', () => {
    const type = button.dataset.removeVideo;
    const input = document.querySelector(`[data-video-upload="${type}"]`);
    const card = document.querySelector(`[data-upload-card="${type}"]`);
    const result = document.querySelector(`[data-upload-result="${type}"]`);
    input.value = '';
    card.classList.remove('has-file');
    result.hidden = true;
    const player = document.querySelector(`[data-video-player="${type}"]`);
    if (player.dataset.objectUrl) URL.revokeObjectURL(player.dataset.objectUrl);
    player.pause();
    player.removeAttribute('src');
    player.load();
    player.hidden = true;
    updateUploadRequirement();
  }));

  const setSectionDisabled = (section, disabled) => {
    section.querySelectorAll('input, textarea, select').forEach(field => {
      if (field.id !== 'price-preview') field.disabled = disabled;
    });
  };

  const applyState = () => {
    const state = states[stateSelect.value];
    const isPublished = stateSelect.value === 'published';
    stateBadge.textContent = state.label;
    stateBadge.className = state.className;
    editorNotice.textContent = state.notice;
    preReviewSections.forEach(section => setSectionDisabled(section, !state.editable));
    submitReview.disabled = !state.editable;
    submitReview.textContent = stateSelect.value === 'rejected' ? '修改後重新送審' : '送出審核';
    saveCourse.disabled = !state.editable;
    saveCourse.textContent = stateSelect.value === 'rejected' ? '儲存修改' : '儲存草稿';

    discountInputs.forEach(input => input.disabled = !isPublished);
    saveDiscount.disabled = !isPublished;
    discountSection.classList.toggle('is-locked', !isPublished);
    discountSection.classList.toggle('is-open', isPublished);
    discountStatus.textContent = isPublished ? '可設定折扣' : '尚未開放';
    discountStatus.className = isPublished ? 'status success' : 'status danger';
    discountLock.hidden = isPublished;
    updateDiscountPrice();
  };

  const showMessage = message => {
    const toast = document.querySelector('.toast');
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add('show');
    window.setTimeout(() => toast.classList.remove('show'), 2200);
  };

  if (window.location.hash === '#discount') stateSelect.value = 'published';

  const minStart = tomorrowAtMidnight();
  discountStart.min = minStart;
  if (!discountStart.value) discountStart.value = minStart;
  if (!discountEnd.value) {
    const end = new Date(`${minStart}:00`);
    end.setDate(end.getDate() + 7);
    const localEnd = new Date(end.getTime() - end.getTimezoneOffset() * 60000);
    discountEnd.value = localEnd.toISOString().slice(0, 16);
  }

  stateSelect.addEventListener('change', applyState);
  price.addEventListener('input', updateDiscountPrice);
  discountRate.addEventListener('change', updateDiscountPrice);
  quickRateButtons.forEach(button => button.addEventListener('click', () => {
    discountRate.value = button.dataset.quickRate;
    otherRateField.hidden = true;
    updateDiscountPrice();
  }));
  toggleOtherRate.addEventListener('click', () => {
    otherRateField.hidden = !otherRateField.hidden;
    updateDiscountPrice();
    if (!otherRateField.hidden) discountRate.focus();
  });
  clearDiscount.addEventListener('click', () => {
    discountRate.value = '';
    otherRateField.hidden = true;
    updateDiscountPrice();
  });
  discountStart.addEventListener('change', () => {
    discountStart.classList.toggle('validation-error', discountStart.value < minStart);
  });

  submitReview.addEventListener('click', () => {
    if (!states[stateSelect.value].editable) return;
    if (!updateUploadRequirement()) {
      videoUploads.filter(input => input.files.length === 0).forEach(input => {
        document.querySelector(`[data-upload-card="${input.dataset.videoUpload}"]`).classList.add('upload-error');
      });
      showMessage('送審失敗：完整課程影片與課程預覽影片都是必填項目');
      return;
    }
    if (!courseForm.reportValidity()) return;
    stateSelect.value = 'reviewing';
    applyState();
    showMessage('課程已送出審核，資料與定價已鎖定');
  });

  courseForm.addEventListener('submit', event => {
    event.preventDefault();
    if (states[stateSelect.value].editable) showMessage(stateSelect.value === 'rejected' ? '修改內容已儲存' : '課程草稿已儲存');
  });

  saveDiscount.addEventListener('click', () => {
    if (stateSelect.value !== 'published') {
      showMessage('只有已上架課程可以設定折扣');
      return;
    }
    if (!discountRate.value) {
      showMessage('現有折扣已取消，課程將恢復原價販售');
      return;
    }
    discountStart.classList.remove('validation-error');
    discountEnd.classList.remove('validation-error');
    if (discountStart.value < minStart) {
      discountStart.classList.add('validation-error');
      showMessage('折扣開始時間最早只能從隔日起算');
      return;
    }
    if (!discountEnd.value || discountEnd.value <= discountStart.value) {
      discountEnd.classList.add('validation-error');
      showMessage('折扣結束時間必須晚於開始時間');
      return;
    }
    showMessage(`${discountRate.options[discountRate.selectedIndex].text}折扣已儲存`);
  });

  updateDiscountPrice();
  updateUploadRequirement();
  applyState();
});
