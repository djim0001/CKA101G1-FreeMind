document.addEventListener('DOMContentLoaded', () => {
  const closeElement = element => {
    if (element) element.style.display = 'none';
  };

  document.querySelectorAll('[data-close-modal]').forEach(button => {
    button.addEventListener('click', () => closeElement(button.closest('.modal, .coupon-modal')));
  });

  document.querySelectorAll('.delist-button').forEach(button => {
    button.addEventListener('click', () => {
      const modal = document.querySelector('#delistModal');
      const idInput = modal?.querySelector('#modalCourseId');
      const idText = modal?.querySelector('#modalCourseIdText');
      const nameText = modal?.querySelector('#modalCourseNameText');
      const reason = modal?.querySelector('#delistReason');
      if (!modal) return;
      if (idInput) idInput.value = button.dataset.courseId || '';
      if (idText) idText.textContent = button.dataset.courseId || '';
      if (nameText) nameText.textContent = button.dataset.courseName || '';
      if (reason) reason.value = '';
      modal.classList.add('show');
    });
  });

  const delistModal = document.querySelector('#delistModal');
  const closeDelistModal = () => delistModal?.classList.remove('show');
  document.querySelector('#closeDelistModal')?.addEventListener('click', closeDelistModal);
  document.querySelector('#cancelDelistButton')?.addEventListener('click', closeDelistModal);
  delistModal?.addEventListener('click', event => {
    if (event.target === delistModal) closeDelistModal();
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') closeDelistModal();
  });
});
