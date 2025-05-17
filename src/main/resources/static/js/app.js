document.addEventListener("DOMContentLoaded", () => {
    //~Initialize variable~//
    const currentUrl = window.location.pathname;

    //~Splash Screen~//
    if (currentUrl === "/") {
      //Splash to redirect
      setTimeout(() => {
        window.location.href = "/login";
      }, 300);
    }

    //~Login~//
    if (currentUrl === "/login") {
      //Initialize document
      const login = document.querySelector(".login");
      const formControl = document.querySelectorAll(".form-control");

      //Ckecking Validation
      if(login.dataset.error) {
        login.classList.add("show");
        formControl.forEach(input => {
          input.classList.add("is-invalid");
        });
      } else {
        setTimeout(() => {
          login.classList.add("show");
        }, 300);
      }

      //Remove Validation when Input
      formControl.forEach(input => {
        input.addEventListener("input", () => {
          input.classList.remove("is-invalid");
        });
      });
    }

    //~Dashboard~//
    const toggleBtn = document.querySelector(".toggle-btn i");
    const sidebar = document.querySelector(".sidebar");
    const topbar = document.querySelector(".topbar");

    //Sidebar Toggle
    toggleBtn.addEventListener("click", () => {
      sidebar.classList.toggle("collapsed");
      topbar.classList.toggle("collapsed");
    });
});

function hasQueryParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.has(param);
}