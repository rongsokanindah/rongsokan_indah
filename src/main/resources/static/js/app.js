document.addEventListener("DOMContentLoaded", () => {
    //Initialize variable
    const currentUrl = window.location.pathname;

    //Splash to redirect
    if (currentUrl === "/") {
      setTimeout(() => {
        window.location.href = "/login";
      }, 1000);
    }
});