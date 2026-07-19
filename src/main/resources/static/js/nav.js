function bindPop(btnId, popId) {
	const btn = document.getElementById(btnId), pop = document.getElementById(popId);
	if (!btn || !pop) return;
	btn.addEventListener('click', function (e) {
		e.stopPropagation();
		const willOpen = !pop.classList.contains('open');
		document.querySelectorAll('.pop.open').forEach(p => p.classList.remove('open'));
		document.querySelectorAll('[aria-haspopup]').forEach(b => b.setAttribute('aria-expanded', 'false'));
		if (willOpen) { pop.classList.add('open'); btn.setAttribute('aria-expanded', 'true'); }
	});
}
bindPop('avatarBtn', 'avatarPop');
bindPop('bellBtn', 'bellPop');

document.addEventListener('click', () => {
	document.querySelectorAll('.pop.open').forEach(p => p.classList.remove('open'));
	document.querySelectorAll('[aria-haspopup]').forEach(b => b.setAttribute('aria-expanded', 'false'));
});

document.addEventListener('keydown', e => {
	if (e.key === 'Escape') document.querySelectorAll('.pop.open').forEach(p => p.classList.remove('open'));
});

const hamb = document.getElementById('hambBtn'), mob = document.getElementById('mobileMenu');
if (hamb && mob) hamb.addEventListener('click', e => { e.stopPropagation(); mob.classList.toggle('open'); });

const backToTopBtn = document.getElementById('backToTop');
const siteFooter = document.getElementById('siteFooter');

if (backToTopBtn) {
	window.addEventListener('scroll', () => {
		if (window.scrollY > 480) {
			backToTopBtn.classList.add('show');
		} else {
			backToTopBtn.classList.remove('show');
		}

		if (siteFooter) {
			const footerRect = siteFooter.getBoundingClientRect();
			const viewportHeight = window.innerHeight;
			const overlap = viewportHeight - footerRect.top;

			if (overlap > 0) {
				backToTopBtn.style.transform = `translateY(-${overlap + 16}px)`;
			} else {
				backToTopBtn.style.transform = '';
			}
		}
	});

	backToTopBtn.addEventListener('click', () => {
		window.scrollTo({ top: 0, behavior: 'smooth' });
	});
}