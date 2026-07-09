const courseVideo = document.querySelector("#courseVideo");
const heroPlayButton = document.querySelector("#heroPlayButton");
const returnUrlInputs = document.querySelectorAll('input[name="returnUrl"]');
const noteInput = document.querySelector("#noteInput");
const saveNoteButton = document.querySelector("#saveNoteButton");
const saveState = document.querySelector("#saveState");

returnUrlInputs.forEach((input) => {
  input.value = window.location.pathname + window.location.search;
});

if (courseVideo && heroPlayButton) {
  const syncPlayButton = () => {
    heroPlayButton.style.display = courseVideo.paused ? "grid" : "none";
  };

  heroPlayButton.addEventListener("click", () => {
    courseVideo.play();
  });

  courseVideo.addEventListener("play", syncPlayButton);
  courseVideo.addEventListener("pause", syncPlayButton);
  courseVideo.addEventListener("ended", syncPlayButton);
  syncPlayButton();
}

if (noteInput && saveNoteButton && saveState) {
  const courseIdInput = document.querySelector('input[name="courseId"]');
  const noteKey = `course-note-${courseIdInput?.value || "default"}`;
  const savedNote = localStorage.getItem(noteKey);

  if (savedNote) {
    noteInput.value = savedNote;
  }

  saveNoteButton.addEventListener("click", () => {
    localStorage.setItem(noteKey, noteInput.value);
    saveState.textContent = "已儲存";
    window.setTimeout(() => {
      saveState.textContent = "";
    }, 1800);
  });
}

function closeCartModal() {
  const cartModal = document.querySelector("#cartModal");
  if (cartModal) {
    cartModal.classList.add("is-hidden");
  }
}
