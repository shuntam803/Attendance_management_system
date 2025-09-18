const buttons = document.querySelectorAll('[data-link]');
buttons.forEach((button) => {
  button.addEventListener('click', () => {
    const link = button.getAttribute('data-link');
    if (link) {
      window.location.href = link;
    }
  });
});
