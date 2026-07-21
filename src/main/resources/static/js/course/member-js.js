document.addEventListener('DOMContentLoaded', () => {
  const toast = document.querySelector('.toast') || Object.assign(document.body.appendChild(document.createElement('div')), { className: 'toast' });
  const cartMessageDuration = 1000;
  let toastTimer;
  let cartMessageTimer;
  const notify = (message, duration = 4200) => {
    if (!message) return;
    toast.textContent = message;
    toast.setAttribute('role', 'status');
    toast.setAttribute('aria-live', 'polite');
    toast.hidden = false;
    toast.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => {
      toast.classList.remove('show');
      toast.hidden = true;
    }, duration);
  };

  const showCartMessage = message => {
    if (!message) return;

    const cartMessage = document.querySelector('[data-cart-message]');
    if (!cartMessage) {
      notify(message, cartMessageDuration);
      return;
    }

    cartMessage.textContent = message;
    cartMessage.hidden = false;
    clearTimeout(cartMessageTimer);
    cartMessageTimer = setTimeout(() => {
      cartMessage.hidden = true;
    }, cartMessageDuration);
  };

  document.querySelectorAll('[data-toast]').forEach(element => element.addEventListener('click', () => notify(element.dataset.toast)));
  const serverCartMessage = document.querySelector('[data-cart-message]');
  if (serverCartMessage?.textContent.trim()) {
    showCartMessage(serverCartMessage.textContent.trim());
  }

  const syncMemberUnreadCount = () => {
    const rawCount = document.body.dataset.memberUnreadCount?.trim();
    if (rawCount === undefined || rawCount === '') return;

    const unreadCount = Number.parseInt(rawCount, 10);
    if (!Number.isFinite(unreadCount) || unreadCount < 0) return;

    const bellButton = document.getElementById('bellBtn');
    if (!bellButton) return;

    let unreadDot = bellButton.querySelector('.dot');
    if (unreadCount > 0 && !unreadDot) {
      unreadDot = document.createElement('span');
      unreadDot.className = 'dot';
      unreadDot.setAttribute('aria-hidden', 'true');
      bellButton.appendChild(unreadDot);
    } else if (unreadCount === 0) {
      unreadDot?.remove();
    }

    const unreadLabel = unreadCount > 0
      ? `你有 ${unreadCount} 則未讀訊息`
      : '目前沒有未讀訊息';
    const unreadCopy = document.querySelector('#bellPop .pop-head p');
    if (unreadCopy) unreadCopy.textContent = unreadLabel;
    bellButton.setAttribute('aria-label', `會員通知，${unreadLabel}`);
  };
  syncMemberUnreadCount();

  const updateCartCount = rawCount => {
    const cartCount = Number.parseInt(String(rawCount).trim(), 10);
    if (!Number.isFinite(cartCount) || cartCount < 0) return false;

    const countLabel = cartCount > 0
      ? `購物車內有 ${cartCount} 門課程`
      : '購物車目前沒有課程';

    document.querySelectorAll('[data-cart-count]').forEach(badge => {
      badge.textContent = String(cartCount);
      badge.hidden = cartCount <= 0;
      badge.setAttribute('aria-label', countLabel);
    });

    document.querySelectorAll('.floating-cart').forEach(cartLink => {
      cartLink.setAttribute('aria-label', `前往購物車，${countLabel}`);
      cartLink.setAttribute('title', countLabel);
    });

    return true;
  };

  document.querySelectorAll('form[action$="/member/course/add_cart"]').forEach(form => {
    form.addEventListener('submit', async event => {
      if (!window.fetch || form.dataset.cartSubmitting === 'true') return;

      event.preventDefault();
      form.dataset.cartSubmitting = 'true';
      const submitButton = form.querySelector('[type="submit"]');
      if (submitButton) submitButton.disabled = true;

      try {
        const response = await fetch(form.action, {
          method: 'POST',
          body: new FormData(form),
          credentials: 'same-origin'
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const responseHtml = await response.text();
        const responseDocument = new DOMParser().parseFromString(responseHtml, 'text/html');
        const responseCartCount = responseDocument.querySelector('[data-cart-count]');
        const responseMessage = responseDocument.querySelector('[data-cart-message]')?.textContent.trim();
        const resolvedCartCount = responseCartCount?.dataset.cartCountValue?.trim();
        const hasResolvedCartCount = resolvedCartCount !== undefined && resolvedCartCount !== '';
        let countUpdated = false;

        if (hasResolvedCartCount) {
          countUpdated = updateCartCount(resolvedCartCount);
        }

        if (!countUpdated) {
          if (response.redirected && /login/i.test(response.url)) {
            window.location.assign(response.url);
          } else if (responseMessage) {
            showCartMessage(responseMessage);
            window.setTimeout(() => window.location.reload(), cartMessageDuration);
          } else {
            window.location.reload();
          }
          return;
        }

        if (responseMessage) showCartMessage(responseMessage);
      } catch (error) {
        console.error('更新購物車數量失敗', error);
        window.location.reload();
      } finally {
        delete form.dataset.cartSubmitting;
        if (submitButton) submitButton.disabled = false;
      }
    });
  });

  const usesSharedNav = Boolean(document.querySelector('script[src="/js/nav.js"]'));
  if (!usesSharedNav) {
  const popBindings = [
    ['bellBtn', 'bellPop'],
    ['avatarBtn', 'avatarPop']
  ].map(([buttonId, popId]) => ({
    button: document.getElementById(buttonId),
    pop: document.getElementById(popId)
  })).filter(binding => binding.button && binding.pop);

  const setPopState = (binding, isOpen) => {
    binding.pop.classList.toggle('open', isOpen);
    binding.button.setAttribute('aria-expanded', String(isOpen));
  };
  const closePops = exceptPop => {
    popBindings.forEach(binding => {
      if (binding.pop !== exceptPop) setPopState(binding, false);
    });
  };

  popBindings.forEach(binding => {
    binding.button.addEventListener('click', event => {
      event.stopPropagation();
      const shouldOpen = !binding.pop.classList.contains('open');
      closePops(binding.pop);
      setPopState(binding, shouldOpen);
    });
    binding.pop.addEventListener('click', event => event.stopPropagation());
  });

  const profileMenuButtons = [...document.querySelectorAll('[data-toggle-menu]')];
  const setProfileMenuState = (button, menu, isOpen) => {
    menu.classList.toggle('open', isOpen);
    button.setAttribute('aria-expanded', String(isOpen));
  };
  const closeProfileMenus = exceptMenu => {
    profileMenuButtons.forEach(button => {
      const menu = document.getElementById(button.dataset.toggleMenu);
      if (!menu || menu === exceptMenu) return;
      setProfileMenuState(button, menu, false);
    });
  };

  profileMenuButtons.forEach(button => {
    const menu = document.getElementById(button.dataset.toggleMenu);
    if (!menu) return;

    button.addEventListener('click', event => {
      event.stopPropagation();
      const shouldOpen = !menu.classList.contains('open');
      closeProfileMenus(menu);
      setProfileMenuState(button, menu, shouldOpen);
    });

    const wrapper = button.closest('.profile-wrapper');
    if (wrapper) {
      wrapper.addEventListener('mouseenter', () => {
        closeProfileMenus(menu);
        setProfileMenuState(button, menu, true);
      });
      wrapper.addEventListener('mouseleave', () => {
        setProfileMenuState(button, menu, false);
      });
    }

    menu.addEventListener('click', event => event.stopPropagation());
  });

  document.addEventListener('click', () => {
    closePops();
    closeProfileMenus();
  });
  document.addEventListener('keydown', event => {
    if (event.key !== 'Escape') return;
    const activePop = popBindings.find(binding => binding.button.getAttribute('aria-expanded') === 'true');
    closePops();
    const activeButton = profileMenuButtons.find(button => button.getAttribute('aria-expanded') === 'true');
    closeProfileMenus();
    activePop?.button.focus();
    activeButton?.focus();
  });

  const hamburgerButton = document.getElementById('memberHambBtn');
  const mobileMenu = document.getElementById('memberMobileMenu');
  if (hamburgerButton && mobileMenu) {
    const setMobileMenuState = isOpen => {
      mobileMenu.classList.toggle('open', isOpen);
      hamburgerButton.setAttribute('aria-expanded', String(isOpen));
      hamburgerButton.setAttribute('aria-label', isOpen ? '關閉選單' : '開啟選單');
    };

    hamburgerButton.addEventListener('click', event => {
      event.stopPropagation();
      setMobileMenuState(!mobileMenu.classList.contains('open'));
    });
    mobileMenu.addEventListener('click', event => event.stopPropagation());
    document.addEventListener('click', () => setMobileMenuState(false));
    document.addEventListener('keydown', event => {
      if (event.key === 'Escape' && mobileMenu.classList.contains('open')) {
        setMobileMenuState(false);
        hamburgerButton.focus();
      }
    });
    window.addEventListener('resize', () => {
      if (window.innerWidth > 940) setMobileMenuState(false);
    });
  }

  const backToTopButton = document.getElementById('backToTop');
  const siteFooter = document.getElementById('siteFooter');
  const floatingCart = document.querySelector('.floating-cart');
  if (backToTopButton) {
    const updateBackToTop = () => {
      backToTopButton.classList.toggle('show', window.scrollY > 480);

      if (floatingCart) {
        backToTopButton.style.transform = '';
        return;
      }
      if (!siteFooter) return;
      const footerOverlap = window.innerHeight - siteFooter.getBoundingClientRect().top;
      backToTopButton.style.transform = footerOverlap > 0
        ? `translateY(-${footerOverlap + 16}px)`
        : '';
    };

    window.addEventListener('scroll', updateBackToTop, { passive: true });
    window.addEventListener('resize', updateBackToTop);
    backToTopButton.addEventListener('click', () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
    updateBackToTop();
  }
  }

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
      [...root.childNodes]
        .filter(node => node.nodeType === Node.TEXT_NODE && node.textContent.trim())
        .forEach(node => {
          const summary = document.createElement('span');
          summary.className = 'pagination-summary';
          summary.textContent = node.textContent.trim();
          node.replaceWith(summary);
        });
      root.querySelectorAll('a').forEach(link => {
        const label = link.textContent.trim();
        const params = new URL(link.href, window.location.href).searchParams;
        const page = params.get('page') || params.get('currentPage');
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

  const modals = [...document.querySelectorAll('.modal-shell')];
  const modalTriggers = new WeakMap();
  const isModalOpen = modal => !modal.hidden && modal.style.display !== 'none';
  const setModalState = (modal, isOpen, trigger = null) => {
    if (!modal) return;

    if (isOpen) {
      if (trigger) modalTriggers.set(modal, trigger);
      modal.hidden = false;
      modal.style.removeProperty('display');
      modal.setAttribute('aria-hidden', 'false');
      window.requestAnimationFrame(() => {
        modal.querySelector('[data-close-modal], input, select, textarea, button, a[href]')?.focus();
      });
      return;
    }

    modal.hidden = true;
    modal.style.display = 'none';
    modal.setAttribute('aria-hidden', 'true');
    const opener = modalTriggers.get(modal);
    if (opener?.isConnected) opener.focus();
  };

  modals.forEach(modal => {
    modal.setAttribute('role', 'dialog');
    modal.setAttribute('aria-modal', 'true');
    if (modal.hidden) {
      setModalState(modal, false);
    } else {
      setModalState(modal, true);
    }

    modal.addEventListener('click', event => {
      if (event.target === modal) setModalState(modal, false);
    });
  });

  document.querySelectorAll('[data-close-modal]').forEach(button => {
    button.addEventListener('click', event => {
      event.preventDefault();
      setModalState(button.closest('.modal-shell'), false);
    });
  });

  document.querySelectorAll('.modal-shell[data-open-on-load="true"]').forEach(modal => {
    setModalState(modal, true);
  });

  document.addEventListener('keydown', event => {
    if (event.key !== 'Escape') return;
    const activeModal = [...modals].reverse().find(isModalOpen);
    if (!activeModal) return;
    event.preventDefault();
    setModalState(activeModal, false);
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

    if (target.matches('.modal-shell')) {
      setModalState(target, !isModalOpen(target), button);
      return;
    }

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

    const updatePlaybackTime = () => {
      if (currentPlaybackTime) currentPlaybackTime.textContent = formatPlaybackTime(courseVideo.currentTime);
    };

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
      updatePlaybackTime();
    });
    courseVideo.addEventListener('timeupdate', () => {
      const currentSecond = Math.floor(courseVideo.currentTime);
      updatePlaybackTime();
      if (currentSecond > 0 && currentSecond % 10 === 0 && currentSecond !== lastSavedSecond) {
        lastSavedSecond = currentSecond;
        savePlaybackPosition(currentSecond);
      }
    });
    courseVideo.addEventListener('pause', () => savePlaybackPosition(Math.floor(courseVideo.currentTime)));
    courseVideo.addEventListener('ended', () => savePlaybackPosition(Math.floor(courseVideo.duration)));
  }
});
