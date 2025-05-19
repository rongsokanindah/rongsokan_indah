document.addEventListener("DOMContentLoaded", () => {
    //~ Initialize ~//
    const currentUrl = window.location.pathname;

    //~~~~~ Splash Screen ~~~~~~~~~~~~~~~//
    if (currentUrl === "/") {
      //Splash to redirect
      setTimeout(() => {
        window.location.href = "/login";
      }, 300);
    }

    //~~~~~ Login ~~~~~~~~~~~~~~~//
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

    //~~~~~ After Login Succesffully ~~~~~~~~~~~~~~~//
    if(currentUrl !== "/" && currentUrl !== "/login") {
      //Initialize document
      const csrfToken = document.querySelector("meta[name='csrf-token']").getAttribute("content");
      const csrfHeader = document.querySelector("meta[name='csrf-header']").getAttribute("content");

      const toggleSidebar = document.querySelector(".toggle-btn i");
      const sidebar = document.querySelector(".sidebar");
      const topbar = document.querySelector(".topbar");
      const footer = document.querySelector(".footer");

      const toggleProfile = document.querySelector(".toggle-profile");
      const profile = document.querySelector(".profile");

      //Sidebar Toggle
      toggleSidebar.addEventListener("click", () => {
        sidebar.classList.toggle("collapsed");
        topbar.classList.toggle("collapsed");
        footer.classList.toggle("collapsed");
      });

      //Profile Toggle
      toggleProfile.addEventListener("click", () => {
        profile.classList.toggle("show");
      });

      //Footer
      footer.querySelector(".year").textContent = new Date().getFullYear();

      //~~~~~ logout ~~~~~~~~~~~~~~~//
      const logoutBtn = profile.querySelector("a[href='/logout'");

      logoutBtn.addEventListener("click", (event) => {
        event.preventDefault();

        fetch("/logout", {
          method: "POST",
          headers: {[csrfHeader]: csrfToken}
        }).then(() => {
          window.location.href = "/";
        });
      });
    }
});