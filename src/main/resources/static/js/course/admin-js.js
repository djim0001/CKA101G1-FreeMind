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

  const takeDownDialog = document.querySelector('[data-take-down-dialog]');
  const takeDownCourseName = takeDownDialog?.querySelector('[data-take-down-course]');
  const takeDownCourseId = takeDownDialog?.querySelector('[data-take-down-course-id]');
  const takeDownReason = takeDownDialog?.querySelector('[data-take-down-reason]');

  document.querySelectorAll('[data-open-take-down]').forEach(button => {
    button.addEventListener('click', () => {
      if (!takeDownDialog) return;
      if (takeDownCourseName) takeDownCourseName.textContent = button.dataset.courseName || '';
      if (takeDownCourseId) takeDownCourseId.value = button.dataset.courseId || '';
      if (takeDownReason) takeDownReason.value = '';
      takeDownDialog.showModal();
      takeDownReason?.focus();
    });
  });

  document.querySelectorAll('[data-close-take-down]').forEach(button => {
    button.addEventListener('click', () => takeDownDialog?.close());
  });

  takeDownDialog?.addEventListener('click', event => {
    if (event.target === takeDownDialog) takeDownDialog.close();
  });

  const revenueRows = [...document.querySelectorAll('[data-revenue-row]')];
  const revenueValues = revenueRows.map(row => Number(row.dataset.revenueValue) || 0);
  const highestRevenue = Math.max(0, ...revenueValues);
  revenueRows.forEach((row, index) => {
    const value = revenueValues[index];
    const bar = row.querySelector('[data-revenue-bar]');
    if (!bar) return;
    bar.style.width = `${highestRevenue > 0 ? (value / highestRevenue) * 100 : 0}%`;
    bar.setAttribute('aria-valuemin', '0');
    bar.setAttribute('aria-valuemax', String(highestRevenue));
    bar.setAttribute('aria-valuenow', String(value));
  });

  const couponSpecForm = document.querySelector('[data-coupon-spec-form]');
  const couponDiscount = couponSpecForm?.querySelector('[data-admin-discount]');
  const couponLimit = couponSpecForm?.querySelector('[data-admin-limit]');
  const couponThreshold = couponSpecForm?.querySelector('[data-admin-threshold]');
  const couponExample = document.querySelector('[data-discount-example]');

  const updateCouponExample = () => {
    if (!couponExample) return;
    const discount = Number(couponDiscount?.value);
    const limit = Number(couponLimit?.value);
    const threshold = Number(couponThreshold?.value);
    if (!Number.isFinite(discount) || discount <= 0 || discount >= 1) {
      couponExample.textContent = '填寫折扣倍率後，這裡會顯示規格摘要。';
      return;
    }
    const percentage = Math.round(discount * 100);
    const details = [`折扣為原價的 ${percentage}%`];
    if (Number.isFinite(limit) && limit > 0) details.push(`最高折抵 NT$ ${limit.toLocaleString('zh-TW')}`);
    if (Number.isFinite(threshold) && threshold > 0) details.push(`消費滿 NT$ ${threshold.toLocaleString('zh-TW')} 可使用`);
    couponExample.textContent = details.join('；') + '。';
  };

  [couponDiscount, couponLimit, couponThreshold].forEach(input => {
    input?.addEventListener('input', updateCouponExample);
  });
  updateCouponExample();

  const audienceSelect = document.querySelector('[data-audience-select]');
  const audienceThreshold = document.querySelector('[data-audience-threshold]');
  const audienceThresholdInput = document.querySelector('[data-audience-threshold-input]');
  const updateAudienceThreshold = () => {
    if (!audienceThreshold) return;
    const needsThreshold = audienceSelect?.value === 'spending';
    audienceThreshold.hidden = !needsThreshold;
    if (audienceThresholdInput) audienceThresholdInput.required = needsThreshold;
  };
  audienceSelect?.addEventListener('change', updateAudienceThreshold);
  updateAudienceThreshold();

  document.querySelectorAll('[data-new-coupon]').forEach(link => {
    link.addEventListener('click', () => {
      window.setTimeout(() => document.getElementById('couponName')?.focus(), 0);
    });
  });

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

  document.querySelectorAll('[data-course-thumbnail]').forEach(video => {
    const showPreviewFrame = () => {
      if (!Number.isFinite(video.duration) || video.duration <= 0) return;
      video.currentTime = Math.min(1, Math.max(0.05, video.duration / 10));
    };
    if (video.readyState >= 1) showPreviewFrame();
    else video.addEventListener('loadedmetadata', showPreviewFrame, { once:true });
    video.addEventListener('seeked', () => video.pause(), { once:true });
    video.addEventListener('error', () => video.classList.add('is-unavailable'), { once:true });
  });

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
    const currentPlaybackTime = document.querySelector('[data-current-playback-time]');
    const playbackPercentage = document.querySelector('[data-playback-percentage]');
    const savedSeconds = Number(courseVideo.dataset.playbackSeconds || 0);
    const courseOrderId = Number(courseVideo.dataset.courseOrderId);
    const courseId = Number(courseVideo.dataset.courseId);
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    let lastSavedSecond = -1;

    const formatPlaybackTime = totalSeconds => {
      const safeSeconds = Math.max(0, Math.floor(Number(totalSeconds) || 0));
      const hours = Math.floor(safeSeconds / 3600);
      const minutes = Math.floor((safeSeconds % 3600) / 60);
      const seconds = safeSeconds % 60;
      return [hours, minutes, seconds].map(value => String(value).padStart(2, '0')).join(':');
    };

    const getPlaybackPercentage = () => {
      if (!Number.isFinite(courseVideo.duration) || courseVideo.duration <= 0) return 0;
      return Math.min(100, Math.max(
        0,
        Number(((courseVideo.currentTime / courseVideo.duration) * 100).toFixed(2))
      ));
    };

    const updatePlaybackStatus = () => {
      if (currentPlaybackTime) currentPlaybackTime.textContent = formatPlaybackTime(courseVideo.currentTime);
      if (playbackPercentage) playbackPercentage.textContent = `${getPlaybackPercentage()}%`;
    };

    const savePlaybackPosition = seconds => {
      if (!Number.isFinite(seconds) || seconds < 0 || !courseOrderId || !courseId) return;
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
      fetch('/member/course/playback-position', {
        method: 'POST',
        headers,
        body: JSON.stringify({
          courseOrderId,
          courseId,
          playbackSeconds: seconds,
          playbackPercentage: getPlaybackPercentage()
        })
      }).then(response => {
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
      }).catch(error => console.error('儲存影片進度失敗', error));
    };

    courseVideo.addEventListener('loadedmetadata', () => {
      if (savedSeconds > 0 && savedSeconds < courseVideo.duration) courseVideo.currentTime = savedSeconds;
      updatePlaybackStatus();
    });
    courseVideo.addEventListener('timeupdate', () => {
      const currentSecond = Math.floor(courseVideo.currentTime);
      updatePlaybackStatus();
      if (currentSecond > 0 && currentSecond % 10 === 0 && currentSecond !== lastSavedSecond) {
        lastSavedSecond = currentSecond;
        savePlaybackPosition(currentSecond);
      }
    });
    courseVideo.addEventListener('pause', () => savePlaybackPosition(Math.floor(courseVideo.currentTime)));
    courseVideo.addEventListener('ended', () => savePlaybackPosition(Math.floor(courseVideo.duration)));
  }
});
