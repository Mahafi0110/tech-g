(function () {
  // ---------- Mobile menu ----------
  const navLinks = document.getElementById('navLinks');
  document.getElementById('menuBtn').addEventListener('click', () => navLinks.classList.toggle('open'));
  navLinks.querySelectorAll('a').forEach(a => a.addEventListener('click', () => navLinks.classList.remove('open')));

  // ---------- Toast ----------
  const toast = document.getElementById('toast');
  if (toast) setTimeout(() => toast.classList.add('hide'), 6000);

  document.getElementById('year').textContent = new Date().getFullYear();

  // ---------- Enquiry tabs ----------
  const TYPES = {
    'IT Products': {
      icon: 'fa-cube', title: 'IT Products Quotation',
      desc: 'For laptops, desktops, accessories and other computer products.',
      label: 'Select Product', ph: 'e.g., Laptop, Monitor, Keyboard', list: () => PRODUCT_NAMES
    },
    'Whole Office Setup': {
      icon: 'fa-desktop', title: 'Whole Office Setup Quotation',
      desc: 'Computers, networking and accessories for a complete office.',
      label: 'Select Main Item', ph: 'Choose the main item for your office', list: () => ['Complete office setup'].concat(PRODUCT_NAMES)
    },
    'Repair and Services': {
      icon: 'fa-wrench', title: 'Repair and Services Request',
      desc: 'Repairs, upgrades, network setup and maintenance plans.',
      label: 'Select Service', ph: 'e.g., Laptop Service, Slow Performance', list: () => SERVICE_NAMES
    }
  };

  const typeInput = document.getElementById('enquiryType');
  const select = document.getElementById('productSelect');
  const tabs = document.querySelectorAll('#enqTabs .tab');

  function fillSelect(list, placeholder, selected) {
    select.innerHTML = '';
    const ph = document.createElement('option');
    ph.value = ''; ph.disabled = true; ph.textContent = placeholder;
    select.appendChild(ph);
    list.forEach(n => {
      const o = document.createElement('option');
      o.value = n; o.textContent = n;
      select.appendChild(o);
    });
    if (selected && list.includes(selected)) select.value = selected; else ph.selected = true;
  }

  function setType(type, selectedItem) {
    const t = TYPES[type];
    typeInput.value = type;
    tabs.forEach(b => b.classList.toggle('active', b.dataset.type === type));
    document.getElementById('tabTitle').textContent = t.title;
    document.getElementById('tabDesc').textContent = t.desc;
    document.getElementById('prodLabel').textContent = t.label;
    document.getElementById('tabIcon').innerHTML = '<i class="fa-solid ' + t.icon + '"></i>';
    fillSelect(t.list(), t.ph, selectedItem);
  }

  tabs.forEach(b => b.addEventListener('click', () => setType(b.dataset.type)));
  setType('IT Products');

  function jumpToForm() {
    document.getElementById('contact').scrollIntoView({ behavior: 'smooth' });
  }

  // "Enquire Now" on a product card picks that product automatically
  document.querySelectorAll('.enquire-product').forEach(btn => btn.addEventListener('click', () => {
    setType('IT Products', btn.dataset.name);
    jumpToForm();
  }));
  // "Book a service" on a service card
  document.querySelectorAll('.enquire-service').forEach(btn => btn.addEventListener('click', () => {
    setType('Repair and Services', btn.dataset.name);
    jumpToForm();
  }));

  // ---------- Reviews carousel ----------
  const track = document.getElementById('revTrack');
  if (track) {
    const step = () => Math.max(track.clientWidth * 0.8, 280);
    document.getElementById('revPrev').addEventListener('click', () => track.scrollBy({ left: -step(), behavior: 'smooth' }));
    document.getElementById('revNext').addEventListener('click', () => track.scrollBy({ left: step(), behavior: 'smooth' }));
  }

  // ---------- Scroll reveals ----------
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (!reduceMotion) {
    const revealTargets = document.querySelectorAll(
      'main > section, footer, .service-card, .product-card, .pillar, .review-card, .why-card, .review-form, .enquiry-form, .show-img, .highlights'
    );

    if ('IntersectionObserver' in window) {
      const revealObserver = new IntersectionObserver(entries => {
        entries.forEach(entry => {
          if (!entry.isIntersecting) return;
          entry.target.classList.add('is-visible');
          revealObserver.unobserve(entry.target);
        });
      }, { threshold: 0.12, rootMargin: '0px 0px -32px 0px' });

      revealTargets.forEach((element, index) => {
        element.classList.add('scroll-reveal');
        element.style.setProperty('--reveal-delay', (index % 4) * 70 + 'ms');
        revealObserver.observe(element);
      });
    }
  }
})();
