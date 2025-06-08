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
    if (login.dataset.error) {
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
  if (currentUrl !== "/" && currentUrl !== "/login") {
    //Initialize document
    const csrfToken = document.querySelector("meta[name='csrf-token']").getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='csrf-header']").getAttribute("content");

    const toggleSidebar = document.querySelector(".toggle-btn i");
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");
    const topbar = document.querySelector(".topbar");
    const footer = document.querySelector(".footer");

    const toggleProfile = document.querySelector(".toggle-profile");
    const profile = document.querySelector(".profile");

    //Sidebar Active
    sidebar.querySelectorAll(".menu a").forEach((menu) => {
      menu.classList.remove("sidebar-active");

      if (menu.getAttribute("href") === currentUrl) {
        menu.classList.add("sidebar-active");
      }
    });

    //Sidebar Toggle
    toggleSidebar.addEventListener("click", () => {
      sidebar.classList.toggle("collapsed");
      content.classList.toggle("collapsed");
      topbar.classList.toggle("collapsed");
      footer.classList.toggle("collapsed");
    });

    //Profile Toggle
    toggleProfile.addEventListener("click", () => {
      profile.classList.toggle("show");
    });

    //Footer
    footer.querySelector(".year").textContent = new Date().getFullYear();

    //Show Tooltip
    document.querySelectorAll("[data-bs-toggle='modal']").forEach((tooltip) => {
      new bootstrap.Tooltip(tooltip);
    });

    //~~~~~ logout ~~~~~~~~~~~~~~~//
    logout();
    function logout() {
      const logoutBtn = profile.querySelector("a[href='/logout'");

      logoutBtn.addEventListener("click", (event) => {
        event.preventDefault();

        fetch("/logout", {
          method: "POST",
          headers: { [csrfHeader]: csrfToken }
        }).then(() => {
          window.location.href = "/";
        });
      });
    }

    //~~~~~ Barang ~~~~~~~~~~~~~~~//
    if (currentUrl === "/barang") {
      barang();
      function barang() {
        //Initialize document
        const barangModal = document.getElementById("barang");
        const barangForm = barangModal.querySelector("form");
        const namaBarang = barangForm.querySelector("#namaBarang");
        const hargaPerKg = barangForm.querySelector("#hargaPerKg");
        const buttonSave = barangModal.querySelector("button[type='submit'");

        //Reset Modal Event
        barangModal.removeEventListener("show.bs.modal", handleShowModal);
        barangModal.addEventListener("show.bs.modal", handleShowModal);

        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          barangModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          barangForm.reset();
          barangForm.classList.remove("was-validated");
          barangForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          barangForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          barangModal.querySelectorAll("button").forEach((button) => button.disabled = false);

          //Data to Form
          if (modalData) {
            const barang = JSON.parse(modalData);
            namaBarang.value = barang.namaBarang;
            hargaPerKg.value = barang.hargaPerKg;
          }

          //Input Validation
          barangForm.addEventListener("input", (input) => {
            if (input.target.checkValidity()) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          });

          //Reset Save Event
          buttonSave.removeEventListener("click", handleSaveClick);
          buttonSave.addEventListener("click", handleSaveClick);
        }

        function handleSaveClick(event) {
          event.preventDefault();

          if (barangForm.checkValidity()) {
            //Disabled All Inputs and Show Loading
            barangModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            barangForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const edit = modalData ? true : false;
            const data = edit ? JSON.parse(modalData) : {};

            //Add or Edit Barang
            fetch("/api/barang", {
              method: edit ? "PUT" : "POST",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: edit ? data.id : "",
                namaBarang: namaBarang.value,
                hargaPerKg: hargaPerKg.value
              })
            }).then((response) => {
              if (response.ok) {
                return response.json();
              }
            }).then((dataResponse) => {

              //Reload Content
              fetch("/barang", {
                method: "POST",
                headers: {
                  [csrfHeader]: csrfToken
                }
              }).then((response) => {
                if (response.ok) {
                  return response.text();
                }
              }).then((html) => {
                content.innerHTML = html;
                deleteBarang();
                barang();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");
                const targetRow = tableBody.querySelectorAll("tr");

                targetRow.forEach((row) => {
                  if (row.dataset.id === targetID) {
                    row.classList.add(edit ? "row-edit-data" : "row-add-data");
                    setTimeout(() => row.className = "", 1500);
                  }
                });
              });
              bootstrap.Modal.getInstance(barangModal).hide();
            });
          } else {
            barangForm.classList.add("was-validated");
          }
        }
      }

      deleteBarang();
      function deleteBarang() {
        //Initialize document
        const confirmModal = document.getElementById("delete");
        const buttonDelete = confirmModal.querySelector("button[type='submit'");

        //Reset Modal Event
        confirmModal.removeEventListener("show.bs.modal", handleShowModal);
        confirmModal.addEventListener("show.bs.modal", handleShowModal);

        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          confirmModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Confirmation
          buttonDelete.querySelector(".spinner").classList.add("d-none");
          confirmModal.querySelectorAll("button").forEach((button) => button.disabled = false);

          //Confirmation Data to Delete
          if (modalData) {
            const barang = JSON.parse(modalData);
            confirmModal.querySelector(".delete-data").textContent = barang.namaBarang;
          }

          //Reset Delete Event
          buttonDelete.removeEventListener("click", handleDeleteClick);
          buttonDelete.addEventListener("click", handleDeleteClick);
        }

        function handleDeleteClick(event) {
          event.preventDefault();

          //Disabled All Button and Show Loading
          confirmModal.querySelectorAll("button").forEach((button) => button.disabled = true);
          buttonDelete.querySelector(".spinner").classList.remove("d-none");

          const data = JSON.parse(modalData);
          const targetID = data.id;

          //Delete Barang
          fetch(`/api/barang/${targetID}`, {
            method: "DELETE",
            headers: {
              [csrfHeader]: csrfToken,
              "Content-Type": "application/json"
            }
          }).then((response) => {
            if (response.ok) {

              //Show Effect Deleted
              const tableBody = content.querySelector("tbody");
              const targetRow = tableBody.querySelectorAll("tr");

              targetRow.forEach((row) => {
                if (row.dataset.id === targetID) {
                  row.classList.add("row-delete-data");

                  //Reload Content
                  setTimeout(() => {
                    fetch("/barang", {
                      method: "POST",
                      headers: {
                        [csrfHeader]: csrfToken
                      }
                    }).then((response) => {
                      if (response.ok) {
                        return response.text();
                      }
                    }).then((html) => {
                      content.innerHTML = html;
                      deleteBarang();
                      barang();
                    });
                  }, 1000);
                }
              });
            }
            bootstrap.Modal.getInstance(confirmModal).hide();
          });
        }
      }
    }
  }
});