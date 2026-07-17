/* ============================================================
   登入頁共用互動（會員 front-end/login.html、管理員 back-end/login.html）
   1. 密碼顯示/隱藏切換（.pw-toggle）
   2. 送出時按鈕 loading + 防重複送出（form.login-form / .btn-submit）
   3. 成功類訊息自動淡出（.alert-success；錯誤訊息保留不動）
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {

	// ---------- 1. 密碼顯示/隱藏 ----------
	document.querySelectorAll('.pw-toggle').forEach(function (btn) {
		btn.addEventListener('click', function () {
			var input = btn.closest('.pw-wrap').querySelector('input');
			var show = input.type === 'password';
			input.type = show ? 'text' : 'password';
			btn.classList.toggle('showing', show);
			btn.setAttribute('aria-label', show ? '隱藏密碼' : '顯示密碼');
			input.focus();
		});
	});

	// ---------- 2. 送出 loading ----------
	document.querySelectorAll('form.login-form').forEach(function (form) {
		form.addEventListener('submit', function () {
			var btn = form.querySelector('.btn-submit');
			if (!btn || btn.classList.contains('is-loading')) {
				return;
			}
			btn.classList.add('is-loading');
			// 延後一拍再 disable：先讓瀏覽器把表單（含按鈕值）送出去
			setTimeout(function () {
				btn.disabled = true;
			}, 0);
		});
	});

	// ---------- 3. 成功訊息自動淡出 ----------
	document.querySelectorAll('.alert-success').forEach(function (el) {
		setTimeout(function () {
			el.classList.add('fade-out');
			el.addEventListener('transitionend', function () {
				el.remove();
			}, { once: true });
		}, 4000);
	});

});
