document.addEventListener("DOMContentLoaded", () => {
  //~ Initialize ~//
  const currentUrl = window.location.pathname;

  //~~~~~ Splash Screen ~~~~~~~~~~~~~~~//
  if (currentUrl === "/") {
    //Splash to Redirect
    setTimeout(() => {
      window.location.href = "/login";
    }, 300);
  }

  //~~~~~ Login ~~~~~~~~~~~~~~~//
  if (currentUrl === "/login") {
    //Initialize document
    const login = document.querySelector(".login");
    const formControl = document.querySelectorAll(".form-control");

    //Checking Validation
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
    const csrfHeaderMeta = document.querySelector("meta[name='csrf-header']");
    const csrfTokenMeta = document.querySelector("meta[name='csrf-token']");
    const csrfHeader = csrfHeaderMeta?.getAttribute("content");
    const csrfToken = csrfTokenMeta?.getAttribute("content");

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
      const logoutBtn = profile.querySelector("a[href='/logout']");

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

    //~~~~~ Kelola Akun ~~~~~~~~~~~~~~~//
    if (currentUrl === "/kelola-akun") {
      kelolaAkun();
      function kelolaAkun() {
        //Initialize document
        const kelolaAkunModal = document.getElementById("kelolaAkun");
        const kelolaAkunForm = kelolaAkunModal.querySelector("form");
        const role = kelolaAkunForm.querySelector("#role");
        const namaAnakBuah = kelolaAkunForm.querySelector("#namaAnakBuah");
        const username = kelolaAkunForm.querySelector("#username");
        const password = kelolaAkunForm.querySelector("#password");
        const konfirmasiPassword = kelolaAkunForm.querySelector("#konfirmasiPassword");
        const buttonSave = kelolaAkunModal.querySelector("button[type='submit']");

        //Default
        const pilihRole = role.textContent;
        const patternPassword = password.getAttribute("pattern");

        //Reset Modal Event
        kelolaAkunModal.removeEventListener("show.bs.modal", handleShowModal);
        kelolaAkunModal.addEventListener("show.bs.modal", handleShowModal);

        let anakBuah = null;
        let roleValue = null;
        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          kelolaAkunModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          kelolaAkunForm.reset();
          kelolaAkunForm.classList.remove("was-validated");
          kelolaAkunForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          kelolaAkunForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          kelolaAkunModal.querySelectorAll("button").forEach((button) => button.disabled = false);

          anakBuah = null;
          roleValue = null;
          role.textContent = pilihRole;

          namaAnakBuah.removeAttribute("required");
          namaAnakBuah.parentElement.classList.add("d-none");
          role.parentElement.classList.remove("is-valid", "is-invalid");
          role.closest(".dropdown").classList.remove("is-valid", "is-invalid");

          password.parentElement.querySelector("label .text-danger").classList.remove("d-none");
          konfirmasiPassword.parentElement.querySelector("label .text-danger").classList.remove("d-none");
          [["required", ""], ["pattern", patternPassword]].forEach(([attr, value]) => password.setAttribute(attr, value));
          [["required", ""], ["pattern", patternPassword]].forEach(([attr, value]) => konfirmasiPassword.setAttribute(attr, value));

          //Data to Form
          if (modalData) {
            const akun = JSON.parse(modalData);

            role.closest(".dropdown").querySelectorAll(".dropdown-menu .dropdown-item").forEach((item) => {
              if (item.dataset.value == akun.role) {
                roleValue = akun.role
                role.textContent = item.textContent;
              }
            });

            if (akun.anakBuah) {
              anakBuah = akun.anakBuah;
              namaAnakBuah.value = akun.anakBuah.nama;

              namaAnakBuah.setAttribute("required", "");
              namaAnakBuah.parentElement.classList.remove("d-none");
            } else {
              anakBuah = null;
              namaAnakBuah.value = "";

              namaAnakBuah.removeAttribute("required");
              namaAnakBuah.parentElement.classList.add("d-none");
            }

            username.value = akun.username;

            ["pattern", "required"].forEach((attr) => password.removeAttribute(attr));
            ["pattern", "required"].forEach((attr) => konfirmasiPassword.removeAttribute(attr));
            password.parentElement.querySelector("label .text-danger").classList.add("d-none");
            konfirmasiPassword.parentElement.querySelector("label .text-danger").classList.add("d-none");
          }

          //Role Handle Select Change
          role.closest(".dropdown").querySelectorAll(".dropdown-menu .dropdown-item").forEach((item) => {
            item.addEventListener("click", (event) => {
              roleValue = event.target.dataset.value;
              role.textContent = event.target.textContent;

              role.parentElement.classList.add("is-valid");
              role.parentElement.classList.remove("is-invalid");
              role.closest(".dropdown").classList.add("is-valid");
              role.closest(".dropdown").classList.remove("is-invalid");

              if (roleValue == "ADMIN") {
                anakBuah = null;
                namaAnakBuah.value = "";

                namaAnakBuah.removeAttribute("required");
                namaAnakBuah.parentElement.classList.add("d-none");
                namaAnakBuah.classList.remove("is-valid", "is-invalid");
              } else {
                namaAnakBuah.setAttribute("required", "");
                namaAnakBuah.parentElement.classList.remove("d-none");
              }
            })
          });

          //Reset Input Event
          kelolaAkunForm.removeEventListener("input", handleInputChange);
          kelolaAkunForm.addEventListener("input", handleInputChange);

          //Reset Save Event
          buttonSave.removeEventListener("click", handleSaveClick);
          buttonSave.addEventListener("click", handleSaveClick);
        }

        function handleInputChange(input) {
          if (input.target == namaAnakBuah) {
            input.target.classList.remove("is-valid");
            input.target.classList.add("is-invalid");
            anakBuah = null;

            if (namaAnakBuah.value != "") {
              //Get Data Dropdown
              fetch(`/api/anak-buah?cari=${namaAnakBuah.value}&sort=nama`, {
                method: "GET",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.json();
              }).then((dataResponse) => {
                //Reset List
                namaAnakBuah.parentElement.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

                //Create Dropdown List
                if (dataResponse.content.length > 0) {
                  //Create Dropdown Menu
                  const ul = document.createElement("ul");
                  ul.style.width = `${namaAnakBuah.offsetWidth}px`;
                  ul.className = "dropdown-menu show py-0";
                  ul.style.marginTop = "-25px";

                  //Create Dropdown Item
                  dataResponse.content.forEach((dataAnakBuah) => {
                    const li = document.createElement("li");
                    const value = new RegExp(namaAnakBuah.value, "gi");
                    const bold = match => `<strong>${match}</strong>`;

                    li.className = "dropdown-item small rounded";
                    li.innerHTML = dataAnakBuah.nama.replace(value, bold);

                    li.addEventListener("click", () => {
                      namaAnakBuah.value = dataAnakBuah.nama
                      anakBuah = dataAnakBuah;

                      input.target.classList.remove("is-invalid");
                      input.target.classList.add("is-valid");
                      ul.remove();
                    });
                    ul.appendChild(li);
                  });
                  namaAnakBuah.parentElement.appendChild(ul);
                }
              });
            } else {
              namaAnakBuah.parentElement.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());
            }
          } else if (input.target == username) {
            const dataError = username.parentElement.querySelector("div");
            const errorMessage = dataError.dataset.error.split("~");

            if (input.target.checkValidity()) {
              //Get Data Pengguna By Username
              fetch(`/api/pengguna?username=${username.value}`, {
                method: "GET",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.json();
              }).then((dataResponse) => {
                //Check Username Used
                if (dataResponse.size == 1) {
                  dataError.textContent = errorMessage[1];
                  input.target.classList.remove("is-valid");
                  input.target.classList.add("is-invalid");
                } else {
                  input.target.classList.remove("is-invalid");
                  input.target.classList.add("is-valid");
                }
              });
            } else {
              dataError.textContent = errorMessage[0];
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          } else if (input.target == konfirmasiPassword) {
            if (konfirmasiPassword.value == password.value) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          } else {
            if (input.target.checkValidity()) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          }
        }

        function handleSaveClick(event) {
          event.preventDefault();

          if (kelolaAkunForm.checkValidity() && roleValue != null && !username.classList.contains("is-invalid") ||
            (!namaAnakBuah.parentElement.classList.contains("d-none") && anakBuah != null)) {

            //Disabled All Inputs and Show Loading
            kelolaAkunModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            kelolaAkunForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const edit = modalData ? true : false;
            const data = edit ? JSON.parse(modalData) : {};

            //Add or Edit Akun
            fetch("/api/pengguna", {
              method: edit ? "PUT" : "POST",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: edit ? data.id : "",
                anakBuah: roleValue == 'ADMIN' ? null : anakBuah,
                username: username.value,
                password: password.value,
                role: roleValue,
              })
            }).then((response) => {
              if (response.ok) return response.json();
            }).then((dataResponse) => {
              const search = getParam("cari");
              const page = getParam("page");

              //Reload Content
              fetch(edit ? `/kelola-akun?cari=${search}&page=${page}` : "/kelola-akun", {
                method: "POST",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.text();
              }).then((html) => {
                content.innerHTML = html;

                //Clear Params If Not Editing
                if (!edit) deleteAllParams();

                //Reinitialize Functions
                kelolaAkun();
                deleteAkun();
                searchAkun();
                paginationAkun();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");

                if (tableBody) {
                  tableBody.querySelectorAll("tr").forEach((row) => {
                    if (row.dataset.id === targetID) {
                      row.classList.add(edit ? "row-edit-data" : "row-add-data");
                      setTimeout(() => row.className = "", 1500);
                    }
                  });
                }
              });
              bootstrap.Modal.getInstance(kelolaAkunModal).hide();
            });
          } else {
            if (roleValue == null) {
              role.parentElement.classList.add("is-invalid")
              role.closest(".dropdown").classList.add("is-invalid");
            }

            if (!namaAnakBuah.parentElement.classList.contains("d-none") && anakBuah == null) {
              namaAnakBuah.classList.add("is-invalid");
            }

            const patternUsername = username.getAttribute("pattern");
            const validationUsername = username.parentElement.querySelector("div");
            if (validationUsername.textContent == "" || !new RegExp(patternUsername).test(username.value)) {
              validationUsername.textContent = validationUsername.dataset.error.split("~")[0];
              username.classList.add("is-invalid");
            }

            if (!new RegExp(patternPassword).test(password.value)) {
              password.classList.add("is-invalid");
            }

            if (konfirmasiPassword.value == "" || password.value != konfirmasiPassword.value) {
              konfirmasiPassword.classList.add("is-invalid");
            }
          }
        }
      }

      deleteAkun();
      function deleteAkun() {
        //Initialize document
        const confirmModal = document.getElementById("delete");
        const buttonDelete = confirmModal.querySelector("button[type='submit']");

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
            const akun = JSON.parse(modalData);
            confirmModal.querySelector(".delete-data").textContent = akun.username;
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

          if (modalData) {
            const data = JSON.parse(modalData);
            const targetID = data.id;

            //Delete Akun
            fetch(`/api/pengguna/${targetID}`, {
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

                    //Get Params
                    let search = getParam("cari");
                    let page = getParam("page");

                    if (targetRow.length == 1) {
                      if (page != 0) {
                        page = page - 1;
                        addParam("page", page);
                      }
                    }

                    //Reload Content
                    setTimeout(() => {
                      fetch(`/kelola-akun?cari=${search}&page=${page}`, {
                        method: "POST",
                        headers: { [csrfHeader]: csrfToken }
                      }).then((response) => {
                        if (response.ok) return response.text();
                      }).then((html) => {
                        content.innerHTML = html;

                        //Reinitialize Functions
                        kelolaAkun();
                        deleteAkun();
                        searchAkun();
                        paginationAkun();
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

      searchAkun();
      function searchAkun() {
        //Initialize document
        const buttonSearchRole = content.querySelector(".search button[type='custom']");
        const buttonSearch = content.querySelector(".search button[type='button']");
        const textSearch = content.querySelector(".search input");

        //Params to Input Search
        textSearch.value = getParam("cari");

        //Show Search Role
        buttonSearchRole.classList.add("dropdown-toggle");
        buttonSearchRole.setAttribute("data-bs-toggle", "dropdown");
        buttonSearchRole.querySelector("i").className = "bi bi-person-workspace";

        const dropdownRole = document.getElementById("role").parentElement.parentElement;
        const dropdownMenuRole = dropdownRole.querySelector(".dropdown-menu");
        const dropdownSearchRole = document.createElement("ul");

        dropdownSearchRole.innerHTML = dropdownMenuRole.innerHTML;
        dropdownSearchRole.className = "dropdown-menu";

        buttonSearchRole.parentElement.appendChild(dropdownSearchRole);
        dropdownSearchRole.querySelectorAll(".dropdown-item").forEach((item) => {
          item.addEventListener("click", (event) => {
            textSearch.value = event.target.dataset.value;
            handleSearchClick();
          });
        });

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          //Get Content
          fetch(`/kelola-akun?cari=${textSearch.value}`, {
            method: "POST",
            headers: { [csrfHeader]: csrfToken }
          }).then((response) => {
            if (response.ok) return response.text();
          }).then((html) => {
            content.innerHTML = html;

            //Update Params
            deleteAllParams();
            addParam("cari", textSearch.value);

            //Reinitialize Functions
            kelolaAkun();
            deleteAkun();
            searchAkun();
            paginationAkun();
          });
        }
      }

      paginationAkun();
      function paginationAkun() {
        //Initialize document
        const pagination = content.querySelector(".pagination");

        //Check Pagination Displayed
        if (pagination) {
          //Reset Page Event
          pagination.removeEventListener("click", handlePageClick);
          pagination.addEventListener("click", handlePageClick);

          //Back or Forward Clicked
          window.addEventListener("popstate", () => location.reload());
        }

        function handlePageClick(event) {
          const target = event.target.closest(".page-item");
          const disabled = target.classList.contains("disabled");
          const active = target.classList.contains("active");
          const dots = target.classList.contains("dots");

          //Checked Not Disabled or Not Active or Not Dots
          if (!disabled && !active && !dots) {
            const pageLink = target.querySelector("a");
            const page = pageLink.dataset.page;
            const hasSearch = hasParam("cari");
            const search = getParam("cari");

            //Get Content
            fetch(hasSearch ? `/kelola-akun?cari=${search}&page=${page}` : `/kelola-akun?page=${page}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              //Add Param
              addParam("page", page);

              //Reinitialize Functions
              kelolaAkun();
              deleteAkun();
              searchAkun();
              paginationAkun();
            });
          }
        }
      }
    }

    //~~~~~ Profil ~~~~~~~~~~~~~~~//
    if (currentUrl === "/profil") {
      profil();
      function profil() {
        //Initialize document
        const profilModal = document.getElementById("profil");
        const profilForm = profilModal.querySelector("form");
        const password = profilForm.querySelector("#password");
        const konfirmasiPassword = profilForm.querySelector("#konfirmasiPassword");
        const buttonSave = profilModal.querySelector("button[type='submit']");

        //Default
        const patternPassword = password.getAttribute("pattern");

        //Reset Modal Event
        profilModal.removeEventListener("show.bs.modal", handleShowModal);
        profilModal.addEventListener("show.bs.modal", handleShowModal);

        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.textContent;

          //Show Modal Title
          profilModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          profilForm.reset();
          profilForm.classList.remove("was-validated");
          profilForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          profilForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          profilModal.querySelectorAll("button").forEach((button) => button.disabled = false);

          //Reset Input Event
          profilForm.removeEventListener("input", handleInputChange);
          profilForm.addEventListener("input", handleInputChange);

          //Reset Save Event
          buttonSave.removeEventListener("click", handleSaveClick);
          buttonSave.addEventListener("click", handleSaveClick);
        }

        function handleInputChange(input) {
          if (input.target == konfirmasiPassword) {
            if (konfirmasiPassword.value == password.value) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          } else {
            if (input.target.checkValidity()) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          }
        }

        function handleSaveClick(event) {
          event.preventDefault();

          const passwordValid = new RegExp(patternPassword).test(password.value);
          const konfirmasiValid = konfirmasiPassword.value != "" && password.value == konfirmasiPassword.value;

          if (passwordValid && konfirmasiValid) {
            //Disabled All Inputs and Show Loading
            profilModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            profilForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const data = modalData ? JSON.parse(modalData) : {};

            //Change Password
            fetch("/api/pengguna", {
              method: "PUT",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: data.id,
                anakBuah: data.anakBuah,
                password: password.value,
              })
            }).then((response) => {
              if (response.ok) return response.json();
            }).then(() => {
              bootstrap.Modal.getInstance(profilModal).hide();
            });
          } else {
            if (!passwordValid) {
              password.classList.add("is-invalid");
            }

            if (!konfirmasiValid) {
              konfirmasiPassword.classList.add("is-invalid");
            }
          }
        }
      }
    }

    //~~~~~ Modal ~~~~~~~~~~~~~~~//
    if (currentUrl === "/modal") {
      modal();
      function modal() {
        //Initialize document
        const modalModal = document.getElementById("modal");
        const modalForm = modalModal?.querySelector("form");
        const namaAnakBuah = modalForm?.querySelector("#namaAnakBuah");
        const jumlahModal = modalForm?.querySelector("#jumlahModal");
        const buttonSave = modalModal?.querySelector("button[type='submit']");

        //Reset Modal Event
        modalModal?.removeEventListener("show.bs.modal", handleShowModal);
        modalModal?.addEventListener("show.bs.modal", handleShowModal);

        let anakBuah = null;
        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          modalModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          modalForm.reset();
          modalForm.classList.remove("was-validated");
          modalForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          modalForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          modalModal.querySelectorAll("button").forEach((button) => button.disabled = false);
          modalModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

          anakBuah = null;

          //Data to Form
          if (modalData) {
            const modal = JSON.parse(modalData);
            namaAnakBuah.value = modal.anakBuah.nama;
            jumlahModal.value = modal.jumlah;
            anakBuah = modal.anakBuah;
          }

          //Reset Input Event
          modalForm.removeEventListener("input", handleInputChange);
          modalForm.addEventListener("input", handleInputChange);

          //Reset Save Event
          buttonSave.removeEventListener("click", handleSaveClick);
          buttonSave.addEventListener("click", handleSaveClick);
        }

        function handleInputChange(input) {
          if (input.target == namaAnakBuah) {
            input.target.classList.remove("is-valid");
            input.target.classList.add("is-invalid");
            anakBuah = null;

            if (namaAnakBuah.value != "") {
              //Get Data Dropdown
              fetch(`/api/anak-buah?cari=${namaAnakBuah.value}&sort=nama`, {
                method: "GET",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.json();
              }).then((dataResponse) => {
                //Reset List
                modalModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

                //Create Dropdown List
                if (dataResponse.content.length > 0) {
                  //Create Dropdown Menu
                  const ul = document.createElement("ul");
                  ul.style.width = `${namaAnakBuah.offsetWidth}px`;
                  ul.className = "dropdown-menu show py-0";
                  ul.style.marginTop = "-25px";

                  //Create Dropdown Item
                  dataResponse.content.forEach((dataAnakBuah) => {
                    const li = document.createElement("li");
                    const value = new RegExp(namaAnakBuah.value, "gi");
                    const bold = match => `<strong>${match}</strong>`;

                    li.className = "dropdown-item small rounded";
                    li.innerHTML = dataAnakBuah.nama.replace(value, bold);

                    li.addEventListener("click", () => {
                      namaAnakBuah.value = dataAnakBuah.nama
                      anakBuah = dataAnakBuah;

                      input.target.classList.remove("is-invalid");
                      input.target.classList.add("is-valid");
                      ul.remove();
                    });
                    ul.appendChild(li);
                  });
                  namaAnakBuah.parentElement.appendChild(ul);
                }
              });
            } else {
              modalModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());
            }
          } else {
            if (input.target.checkValidity()) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          }
        }

        function handleSaveClick(event) {
          event.preventDefault();

          if (modalForm.checkValidity() && anakBuah != null) {
            //Disabled All Inputs and Show Loading
            modalModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            modalForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const edit = modalData ? true : false;
            const data = edit ? JSON.parse(modalData) : {};

            //Add or Edit Modal
            fetch("/api/modal", {
              method: edit ? "PUT" : "POST",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: edit ? data.id : "",
                jumlah: jumlahModal.value,
                anakBuah: anakBuah,
              })
            }).then((response) => {
              if (response.ok) return response.json();
            }).then((dataResponse) => {
              const search = getParam("cari");
              const page = getParam("page");

              //Reload Content
              fetch(edit ? `/modal?cari=${search}&page=${page}` : "/modal", {
                method: "POST",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.text();
              }).then((html) => {
                content.innerHTML = html;

                //Clear Params If Not Editing
                if (!edit) deleteAllParams();

                //Reinitialize Functions
                modal();
                searchModal();
                paginationModal();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");

                if (tableBody) {
                  tableBody.querySelectorAll("tr").forEach((row) => {
                    if (row.dataset.id === targetID) {
                      row.classList.add(edit ? "row-edit-data" : "row-add-data");
                      setTimeout(() => row.className = "", 1500);
                    }
                  });
                }
              });
              bootstrap.Modal.getInstance(modalModal).hide();
            });
          } else {
            if (anakBuah == null) namaAnakBuah.classList.add("is-invalid");
            if (jumlahModal.value < 50000) jumlahModal.classList.add("is-invalid");
          }
        }
      }

      searchModal();
      function searchModal() {
        //Initialize document
        const buttonSearchDate = content.querySelector(".search button[type='custom']");
        const buttonSearch = content.querySelector(".search button[type='button']");
        const textSearch = content.querySelector(".search input");

        //Params to Input Search
        textSearch.value = getParam("cari");

        //Show Date Picker
        flatpickr(buttonSearchDate, {
          locale: "id",
          dateFormat: "d/m/Y",
          onReady: (_, __, instance) => {
            const month = instance.monthsDropdownContainer;
            const year = instance.currentYearElement;

            const textMonthYear = document.createElement("div");
            textMonthYear.className = "custom-month-year";
            updateText();

            month.classList.add("d-none");
            year.parentElement.classList.add("d-none");
            month.parentElement.appendChild(textMonthYear);

            instance.config.onMonthChange.push(updateText);
            instance.config.onYearChange.push(updateText);

            function updateText() {
              const year = instance.currentYear;
              const month = instance.l10n.months.longhand[instance.currentMonth];
              textMonthYear.textContent = `${month} ${year}`;
            }
          },
          onChange: (_, selected, __) => {
            textSearch.value = selected;
            handleSearchClick();
          },
        });

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          //Get Content
          fetch(`/modal?cari=${textSearch.value}`, {
            method: "POST",
            headers: { [csrfHeader]: csrfToken }
          }).then((response) => {
            if (response.ok) return response.text();
          }).then((html) => {
            content.innerHTML = html;

            //Update Params
            deleteAllParams();
            addParam("cari", textSearch.value);

            //Reinitialize Functions
            modal();
            searchModal();
            paginationModal();
          });
        }
      }

      paginationModal();
      function paginationModal() {
        //Initialize document
        const pagination = content.querySelector(".pagination");

        //Check Pagination Displayed
        if (pagination) {
          //Reset Page Event
          pagination.removeEventListener("click", handlePageClick);
          pagination.addEventListener("click", handlePageClick);

          //Back or Forward Clicked
          window.addEventListener("popstate", () => location.reload());
        }

        function handlePageClick(event) {
          const target = event.target.closest(".page-item");
          const disabled = target.classList.contains("disabled");
          const active = target.classList.contains("active");
          const dots = target.classList.contains("dots");

          //Checked Not Disabled or Not Active or Not Dots
          if (!disabled && !active && !dots) {
            const pageLink = target.querySelector("a");
            const page = pageLink.dataset.page;
            const hasSearch = hasParam("cari");
            const search = getParam("cari");

            //Get Content
            fetch(hasSearch ? `/modal?cari=${search}&page=${page}` : `/modal?page=${page}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              //Add Param
              addParam("page", page);

              //Reinitialize Functions
              modal();
              searchModal();
              paginationModal();
            });
          }
        }
      }
    }

    //~~~~~ Barang ~~~~~~~~~~~~~~~//
    if (currentUrl === "/barang") {
      barang();
      function barang() {
        //Initialize document
        const barangModal = document.getElementById("barang");
        const barangForm = barangModal?.querySelector("form");
        const namaBarang = barangForm?.querySelector("#namaBarang");
        const hargaPerKg = barangForm?.querySelector("#hargaPerKg");
        const buttonSave = barangModal?.querySelector("button[type='submit']");

        //Reset Modal Event
        barangModal?.removeEventListener("show.bs.modal", handleShowModal);
        barangModal?.addEventListener("show.bs.modal", handleShowModal);

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
              if (response.ok) return response.json();
            }).then((dataResponse) => {
              const search = getParam("cari");
              const page = getParam("page");

              //Reload Content
              fetch(edit ? `/barang?cari=${search}&page=${page}` : "/barang", {
                method: "POST",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.text();
              }).then((html) => {
                content.innerHTML = html;

                //Clear Params If Not Editing
                if (!edit) deleteAllParams();

                //Reinitialize Functions
                barang();
                deleteBarang();
                searchBarang();
                paginationBarang();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");

                if (tableBody) {
                  tableBody.querySelectorAll("tr").forEach((row) => {
                    if (row.dataset.id === targetID) {
                      row.classList.add(edit ? "row-edit-data" : "row-add-data");
                      setTimeout(() => row.className = "", 1500);
                    }
                  });
                }
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
        const buttonDelete = confirmModal?.querySelector("button[type='submit']");

        //Reset Modal Event
        confirmModal?.removeEventListener("show.bs.modal", handleShowModal);
        confirmModal?.addEventListener("show.bs.modal", handleShowModal);

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

          if (modalData) {
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

                    //Get Params
                    let search = getParam("cari");
                    let page = getParam("page");

                    if (targetRow.length == 1) {
                      if (page != 0) {
                        page = page - 1;
                        addParam("page", page);
                      }
                    }

                    //Reload Content
                    setTimeout(() => {
                      fetch(`/barang?cari=${search}&page=${page}`, {
                        method: "POST",
                        headers: { [csrfHeader]: csrfToken }
                      }).then((response) => {
                        if (response.ok) return response.text();
                      }).then((html) => {
                        content.innerHTML = html;

                        //Reinitialize Functions
                        barang();
                        deleteBarang();
                        searchBarang();
                        paginationBarang();
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

      searchBarang();
      function searchBarang() {
        //Initialize document
        const buttonSearch = content.querySelector(".search button");
        const textSearch = content.querySelector(".search input");

        //Params to Input Search
        textSearch.value = getParam("cari");

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          //Get Content
          fetch(`/barang?cari=${textSearch.value}`, {
            method: "POST",
            headers: { [csrfHeader]: csrfToken }
          }).then((response) => {
            if (response.ok) return response.text();
          }).then((html) => {
            content.innerHTML = html;

            //Update Params
            deleteAllParams();
            addParam("cari", textSearch.value);

            //Reinitialize Functions
            barang();
            deleteBarang();
            searchBarang();
            paginationBarang();
          });
        }
      }

      paginationBarang();
      function paginationBarang() {
        //Initialize document
        const pagination = content.querySelector(".pagination");

        //Check Pagination Displayed
        if (pagination) {
          //Reset Page Event
          pagination.removeEventListener("click", handlePageClick);
          pagination.addEventListener("click", handlePageClick);

          //Back or Forward Clicked
          window.addEventListener("popstate", () => location.reload());
        }

        function handlePageClick(event) {
          const target = event.target.closest(".page-item");
          const disabled = target.classList.contains("disabled");
          const active = target.classList.contains("active");
          const dots = target.classList.contains("dots");

          //Checked Not Disabled or Not Active or Not Dots
          if (!disabled && !active && !dots) {
            const pageLink = target.querySelector("a");
            const page = pageLink.dataset.page;
            const hasSearch = hasParam("cari");
            const search = getParam("cari");

            //Get Content
            fetch(hasSearch ? `/barang?cari=${search}&page=${page}` : `/barang?page=${page}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              //Add Param
              addParam("page", page);

              //Reinitialize Functions
              barang();
              deleteBarang();
              searchBarang();
              paginationBarang();
            });
          }
        }
      }
    }

    //~~~~~ Anak Buah ~~~~~~~~~~~~~~~//
    if (currentUrl === "/anak-buah") {
      anakBuah();
      function anakBuah() {
        //Initialize document
        const anakBuahModal = document.getElementById("anakBuah");
        const anakBuahForm = anakBuahModal.querySelector("form");
        const namaAnakBuah = anakBuahForm.querySelector("#namaAnakBuah");
        const nomorWhatsApp = anakBuahForm.querySelector("#nomorWhatsApp");
        const buttonSave = anakBuahModal.querySelector("button[type='submit']");

        //Reset Modal Event
        anakBuahModal.removeEventListener("show.bs.modal", handleShowModal);
        anakBuahModal.addEventListener("show.bs.modal", handleShowModal);

        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          anakBuahModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          anakBuahForm.reset();
          anakBuahForm.classList.remove("was-validated");
          anakBuahForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          anakBuahForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          anakBuahModal.querySelectorAll("button").forEach((button) => button.disabled = false);

          //Data to Form
          if (modalData) {
            const anakBuah = JSON.parse(modalData);
            namaAnakBuah.value = anakBuah.nama;
            nomorWhatsApp.value = anakBuah.nomorWhatsApp;
          }

          //Input Validation
          anakBuahForm.addEventListener("input", (input) => {
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

          if (anakBuahForm.checkValidity()) {
            //Disabled All Inputs and Show Loading
            anakBuahModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            anakBuahForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const edit = modalData ? true : false;
            const data = edit ? JSON.parse(modalData) : {};

            //Add or Edit Anak Buah
            fetch("/api/anak-buah", {
              method: edit ? "PUT" : "POST",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: edit ? data.id : "",
                nama: namaAnakBuah.value,
                nomorWhatsApp: nomorWhatsApp.value
              })
            }).then((response) => {
              if (response.ok) return response.json();
            }).then((dataResponse) => {
              const search = getParam("cari");
              const page = getParam("page");

              //Reload Content
              fetch(edit ? `/anak-buah?cari=${search}&page=${page}` : "/anak-buah", {
                method: "POST",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.text();
              }).then((html) => {
                content.innerHTML = html;

                //Clear Params If Not Editing
                if (!edit) deleteAllParams();

                //Reinitialize Functions
                anakBuah();
                deleteAnakBuah();
                searchAnakBuah();
                paginationAnakBuah();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");

                if (tableBody) {
                  tableBody.querySelectorAll("tr").forEach((row) => {
                    if (row.dataset.id === targetID) {
                      row.classList.add(edit ? "row-edit-data" : "row-add-data");
                      setTimeout(() => row.className = "", 1500);
                    }
                  });
                }
              });
              bootstrap.Modal.getInstance(anakBuahModal).hide();
            });
          } else {
            anakBuahForm.classList.add("was-validated");
          }
        }
      }

      deleteAnakBuah();
      function deleteAnakBuah() {
        //Initialize document
        const confirmModal = document.getElementById("delete");
        const buttonDelete = confirmModal.querySelector("button[type='submit']");

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
            const anakBuah = JSON.parse(modalData);
            confirmModal.querySelector(".delete-data").textContent = anakBuah.nama;
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

          if (modalData) {
            const data = JSON.parse(modalData);
            const targetID = data.id;

            //Delete Barang
            fetch(`/api/anak-buah/${targetID}`, {
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

                    //Get Params
                    let search = getParam("cari");
                    let page = getParam("page");

                    if (targetRow.length == 1) {
                      if (page != 0) {
                        page = page - 1;
                        addParam("page", page);
                      }
                    }

                    //Reload Content
                    setTimeout(() => {
                      fetch(`/anak-buah?cari=${search}&page=${page}`, {
                        method: "POST",
                        headers: { [csrfHeader]: csrfToken }
                      }).then((response) => {
                        if (response.ok) return response.text();
                      }).then((html) => {
                        content.innerHTML = html;

                        //Reinitialize Functions
                        anakBuah();
                        deleteAnakBuah();
                        searchAnakBuah();
                        paginationAnakBuah();
                      });
                    }, 500);
                  }
                });
              }
              bootstrap.Modal.getInstance(confirmModal).hide();
            });
          }
        }
      }

      searchAnakBuah();
      function searchAnakBuah() {
        //Initialize document
        const buttonSearch = content.querySelector(".search button");
        const textSearch = content.querySelector(".search input");

        //Params to Input Search
        textSearch.value = getParam("cari");

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          //Get Content
          fetch(`/anak-buah?cari=${textSearch.value}`, {
            method: "POST",
            headers: { [csrfHeader]: csrfToken }
          }).then((response) => {
            if (response.ok) return response.text();
          }).then((html) => {
            content.innerHTML = html;

            //Update Params
            deleteAllParams();
            addParam("cari", textSearch.value);

            //Reinitialize Functions
            anakBuah();
            deleteAnakBuah();
            searchAnakBuah();
            paginationAnakBuah();
          });
        }
      }

      paginationAnakBuah();
      function paginationAnakBuah() {
        //Initialize document
        const pagination = content.querySelector(".pagination");

        //Check Pagination Displayed
        if (pagination) {
          //Reset Page Event
          pagination.removeEventListener("click", handlePageClick);
          pagination.addEventListener("click", handlePageClick);

          //Back or Forward Clicked
          window.addEventListener("popstate", () => location.reload());
        }

        function handlePageClick(event) {
          const target = event.target.closest(".page-item");
          const disabled = target.classList.contains("disabled");
          const active = target.classList.contains("active");
          const dots = target.classList.contains("dots");

          //Checked Not Disabled or Not Active or Not Dots
          if (!disabled && !active && !dots) {
            const pageLink = target.querySelector("a");
            const page = pageLink.dataset.page;
            const hasSearch = hasParam("cari");
            const search = getParam("cari");

            //Get Content
            fetch(hasSearch ? `/anak-buah?cari=${search}&page=${page}` : `/anak-buah?page=${page}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              //Add Param
              addParam("page", page);

              //Reinitialize Functions
              anakBuah();
              deleteAnakBuah();
              searchAnakBuah();
              paginationAnakBuah();
            });
          }
        }
      }
    }

    //~~~~~ Transaksi Masuk ~~~~~~~~~~~~~~~//
    if (currentUrl === "/transaksi-masuk") {
      transaksiMasuk();
      function transaksiMasuk() {
        //Initialize document
        const transaksiMasukModal = document.getElementById("transaksiMasuk");
        const transaksiMasukForm = transaksiMasukModal.querySelector("form");
        const namaAnakBuah = transaksiMasukForm.querySelector("#namaAnakBuah");
        const namaBarang = transaksiMasukForm.querySelector("#namaBarang");
        const beratKg = transaksiMasukForm.querySelector("#beratKg");
        const buttonSave = transaksiMasukModal.querySelector("button[type='submit']");

        //Reset Modal Event
        transaksiMasukModal.removeEventListener("show.bs.modal", handleShowModal);
        transaksiMasukModal.addEventListener("show.bs.modal", handleShowModal);

        let barang = null;
        let modalData = null;
        let modalTitle = null;

        //Login Not As Admin
        const dataAnakBuah = namaAnakBuah.parentElement.dataset.anakBuah;
        let anakBuah = dataAnakBuah ? JSON.parse(dataAnakBuah) : null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          transaksiMasukModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          transaksiMasukForm.reset();
          transaksiMasukForm.classList.remove("was-validated");
          transaksiMasukForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          transaksiMasukForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          transaksiMasukModal.querySelectorAll("button").forEach((button) => button.disabled = false);
          transaksiMasukForm.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

          barang = null;
          anakBuah = dataAnakBuah ? JSON.parse(dataAnakBuah) : null;
          namaAnakBuah.value = anakBuah ? anakBuah.nama : "";

          //Data to Form
          if (modalData) {
            const transaksiMasuk = JSON.parse(modalData);
            namaAnakBuah.value = transaksiMasuk.anakBuah.nama;
            namaBarang.value = transaksiMasuk.barang.namaBarang;
            beratKg.value = transaksiMasuk.beratKg;

            anakBuah = transaksiMasuk.anakBuah;
            barang = transaksiMasuk.barang;
          }

          //Reset Input Event
          transaksiMasukForm.removeEventListener("input", handleInputChange);
          transaksiMasukForm.addEventListener("input", handleInputChange);

          //Reset Save Event
          buttonSave.removeEventListener("click", handleSaveClick);
          buttonSave.addEventListener("click", handleSaveClick);
        }

        function handleInputChange(input) {
          if (input.target == namaAnakBuah) {
            input.target.classList.remove("is-valid");
            input.target.classList.add("is-invalid");
            anakBuah = null;

            if (namaAnakBuah.value != "") {
              //Get Data Dropdown
              fetch(`/api/anak-buah?cari=${namaAnakBuah.value}&sort=nama`, {
                method: "GET",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.json();
              }).then((dataResponse) => {
                //Reset List
                transaksiMasukModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

                //Create Dropdown List
                if (dataResponse.content.length > 0) {
                  //Create Dropdown Menu
                  const ul = document.createElement("ul");
                  ul.style.width = `${namaAnakBuah.offsetWidth}px`;
                  ul.className = "dropdown-menu show py-0";
                  ul.style.marginTop = "-25px";

                  //Create Dropdown Item
                  dataResponse.content.forEach((dataAnakBuah) => {
                    const li = document.createElement("li");
                    const value = new RegExp(namaAnakBuah.value, "gi");
                    const bold = match => `<strong>${match}</strong>`;

                    li.className = "dropdown-item small rounded";
                    li.innerHTML = dataAnakBuah.nama.replace(value, bold);

                    li.addEventListener("click", () => {
                      namaAnakBuah.value = dataAnakBuah.nama
                      anakBuah = dataAnakBuah;

                      input.target.classList.remove("is-invalid");
                      input.target.classList.add("is-valid");
                      ul.remove();
                    });
                    ul.appendChild(li);
                  });
                  namaAnakBuah.parentElement.appendChild(ul);
                }
              });
            } else {
              transaksiMasukModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());
            }
          } else if (input.target == namaBarang) {
            input.target.classList.remove("is-valid");
            input.target.classList.add("is-invalid");
            barang = null;

            if (namaBarang.value != "") {
              //Get Data Dropdown
              fetch(`/api/barang?cari=${namaBarang.value}&sort=namaBarang`, {
                method: "GET",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.json();
              }).then((dataResponse) => {
                //Reset List
                transaksiMasukModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

                //Create Dropdown List
                if (dataResponse.content.length > 0) {
                  //Create Dropdown Menu
                  const ul = document.createElement("ul");
                  ul.style.width = `${namaBarang.offsetWidth}px`;
                  ul.className = "dropdown-menu show py-0";
                  ul.style.marginTop = "-25px";

                  //Create Dropdown Item
                  dataResponse.content.forEach((dataBarang) => {
                    const li = document.createElement("li");
                    const value = new RegExp(namaBarang.value, "gi");
                    const bold = match => `<strong>${match}</strong>`;

                    li.className = "dropdown-item small rounded";
                    li.innerHTML = dataBarang.namaBarang.replace(value, bold);

                    li.addEventListener("click", () => {
                      namaBarang.value = dataBarang.namaBarang;
                      barang = dataBarang;

                      input.target.classList.remove("is-invalid");
                      input.target.classList.add("is-valid");
                      ul.remove();
                    });
                    ul.appendChild(li);
                  });
                  namaBarang.parentElement.appendChild(ul);
                }
              });
            } else {
              transaksiMasukModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());
            }
          } else {
            if (input.target.checkValidity()) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          }
        }

        function handleSaveClick(event) {
          event.preventDefault();

          if (transaksiMasukForm.checkValidity() && anakBuah != null && barang != null) {
            //Disabled All Inputs and Show Loading
            transaksiMasukModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            transaksiMasukForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const edit = modalData ? true : false;
            const data = edit ? JSON.parse(modalData) : {};

            //Add or Edit Transaksi Masuk
            fetch("/api/transaksi-masuk", {
              method: edit ? "PUT" : "POST",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: edit ? data.id : "",
                totalHarga: beratKg.value * barang.hargaPerKg,
                beratKg: beratKg.value,
                anakBuah: anakBuah,
                barang: barang,
              })
            }).then((response) => {
              if (response.ok) return response.json();
            }).then((dataResponse) => {
              const search = getParam("cari");
              const page = getParam("page");

              //Reload Content
              fetch(edit ? `/transaksi-masuk?cari=${search}&page=${page}` : "/transaksi-masuk", {
                method: "POST",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.text();
              }).then((html) => {
                content.innerHTML = html;

                //Clear Params If Not Editing
                if (!edit) deleteAllParams();

                //Reinitialize Functions
                transaksiMasuk();
                searchTransaksiMasuk();
                paginationTransaksiMasuk();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");

                if (tableBody) {
                  tableBody.querySelectorAll("tr").forEach((row) => {
                    if (row.dataset.id === targetID) {
                      row.classList.add(edit ? "row-edit-data" : "row-add-data");
                      setTimeout(() => row.className = "", 1500);
                    }
                  });
                }
              });
              bootstrap.Modal.getInstance(transaksiMasukModal).hide();
            });
          } else {
            if (anakBuah == null) namaAnakBuah.classList.add("is-invalid");
            if (barang == null) namaBarang.classList.add("is-invalid");
            if (beratKg.value < 1) beratKg.classList.add("is-invalid");
          }
        }
      }

      searchTransaksiMasuk();
      function searchTransaksiMasuk() {
        //Initialize document
        const buttonSearchDate = content.querySelector(".search button[type='custom']");
        const buttonSearch = content.querySelector(".search button[type='button']");
        const textSearch = content.querySelector(".search input");

        //Params to Input Search
        textSearch.value = getParam("cari");

        //Show Date Picker
        flatpickr(buttonSearchDate, {
          locale: "id",
          dateFormat: "d/m/Y",
          onReady: (_, __, instance) => {
            const month = instance.monthsDropdownContainer;
            const year = instance.currentYearElement;

            const textMonthYear = document.createElement("div");
            textMonthYear.className = "custom-month-year";
            updateText();

            month.classList.add("d-none");
            year.parentElement.classList.add("d-none");
            month.parentElement.appendChild(textMonthYear);

            instance.config.onMonthChange.push(updateText);
            instance.config.onYearChange.push(updateText);

            function updateText() {
              const year = instance.currentYear;
              const month = instance.l10n.months.longhand[instance.currentMonth];
              textMonthYear.textContent = `${month} ${year}`;
            }
          },
          onChange: (_, selected, __) => {
            textSearch.value = selected;
            handleSearchClick();
          },
        });

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          //Get Content
          fetch(`/transaksi-masuk?cari=${textSearch.value}`, {
            method: "POST",
            headers: { [csrfHeader]: csrfToken }
          }).then((response) => {
            if (response.ok) return response.text();
          }).then((html) => {
            content.innerHTML = html;

            //Update Params
            deleteAllParams();
            addParam("cari", textSearch.value);

            //Reinitialize Functions
            transaksiMasuk();
            searchTransaksiMasuk();
            paginationTransaksiMasuk();
          });
        }
      }

      paginationTransaksiMasuk();
      function paginationTransaksiMasuk() {
        //Initialize document
        const pagination = content.querySelector(".pagination");

        //Check Pagination Displayed
        if (pagination) {
          //Reset Page Event
          pagination.removeEventListener("click", handlePageClick);
          pagination.addEventListener("click", handlePageClick);

          //Back or Forward Clicked
          window.addEventListener("popstate", () => location.reload());
        }

        function handlePageClick(event) {
          const target = event.target.closest(".page-item");
          const disabled = target.classList.contains("disabled");
          const active = target.classList.contains("active");
          const dots = target.classList.contains("dots");

          //Checked Not Disabled or Not Active or Not Dots
          if (!disabled && !active && !dots) {
            const pageLink = target.querySelector("a");
            const page = pageLink.dataset.page;
            const hasSearch = hasParam("cari");
            const search = getParam("cari");

            //Get Content
            fetch(hasSearch ? `/transaksi-masuk?cari=${search}&page=${page}` : `/transaksi-masuk?page=${page}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              //Add Param
              addParam("page", page);

              //Reinitialize Functions
              transaksiMasuk();
              searchTransaksiMasuk();
              paginationTransaksiMasuk();
            });
          }
        }
      }
    }

    //~~~~~ Transaksi Keluar ~~~~~~~~~~~~~~~//
    if (currentUrl === "/transaksi-keluar") {
      transaksiKeluar();
      function transaksiKeluar() {
        //Initialize document
        const transaksiKeluarModal = document.getElementById("transaksiKeluar");
        const transaksiKeluarForm = transaksiKeluarModal.querySelector("form");
        const namaBarang = transaksiKeluarForm.querySelector("#namaBarang");
        const beratKg = transaksiKeluarForm.querySelector("#beratKg");
        const hargaJual = transaksiKeluarForm.querySelector("#hargaJual");
        const buttonSave = transaksiKeluarModal.querySelector("button[type='submit']");

        //Reset Modal Event
        transaksiKeluarModal.removeEventListener("show.bs.modal", handleShowModal);
        transaksiKeluarModal.addEventListener("show.bs.modal", handleShowModal);

        let barang = null;
        let modalData = null;
        let modalTitle = null;

        function handleShowModal(event) {
          const buttonShow = event.relatedTarget;
          modalData = buttonShow.dataset.bsData;
          modalTitle = buttonShow.dataset.bsTitle;

          //Show Modal Title
          transaksiKeluarModal.querySelector(".modal-title").textContent = modalTitle;

          //Reset Modal Form
          transaksiKeluarForm.reset();
          transaksiKeluarForm.classList.remove("was-validated");
          transaksiKeluarForm.querySelectorAll("input").forEach((input) => input.classList.remove("is-valid", "is-invalid"));

          buttonSave.querySelector(".spinner").classList.add("d-none");
          transaksiKeluarForm.querySelectorAll("input").forEach((input) => input.disabled = false);
          transaksiKeluarModal.querySelectorAll("button").forEach((button) => button.disabled = false);
          transaksiKeluarForm.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

          barang = null;

          //Data to Form
          if (modalData) {
            const transaksiKeluar = JSON.parse(modalData);
            namaBarang.value = transaksiKeluar.barang.namaBarang;
            hargaJual.value = transaksiKeluar.hargaJual;
            beratKg.value = transaksiKeluar.beratKg;
            barang = transaksiKeluar.barang;
          }

          //Reset Input Event
          transaksiKeluarForm.removeEventListener("input", handleInputChange);
          transaksiKeluarForm.addEventListener("input", handleInputChange);

          //Reset Save Event
          buttonSave.removeEventListener("click", handleSaveClick);
          buttonSave.addEventListener("click", handleSaveClick);
        }

        function handleInputChange(input) {
          if (input.target == namaBarang) {
            input.target.classList.remove("is-valid");
            input.target.classList.add("is-invalid");
            barang = null;

            if (namaBarang.value != "") {
              //Get Data Dropdown
              fetch(`/api/barang?cari=${namaBarang.value}&sort=namaBarang`, {
                method: "GET",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.json();
              }).then((dataResponse) => {
                //Reset List
                transaksiKeluarModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());

                //Create Dropdown List
                if (dataResponse.content.length > 0) {
                  //Create Dropdown Menu
                  const ul = document.createElement("ul");
                  ul.style.width = `${namaBarang.offsetWidth}px`;
                  ul.className = "dropdown-menu show py-0";
                  ul.style.marginTop = "-25px";

                  //Create Dropdown Item
                  dataResponse.content.forEach((dataBarang) => {
                    const li = document.createElement("li");
                    const value = new RegExp(namaBarang.value, "gi");
                    const bold = match => `<strong>${match}</strong>`;

                    li.className = "dropdown-item small rounded";
                    li.innerHTML = dataBarang.namaBarang.replace(value, bold);

                    li.addEventListener("click", () => {
                      namaBarang.value = dataBarang.namaBarang;
                      barang = dataBarang;

                      input.target.classList.remove("is-invalid");
                      input.target.classList.add("is-valid");
                      ul.remove();
                    });
                    ul.appendChild(li);
                  });
                  namaBarang.parentElement.appendChild(ul);
                }
              });
            } else {
              transaksiKeluarModal.querySelectorAll(".dropdown-menu").forEach((dropdown) => dropdown.remove());
            }
          } else {
            if (input.target.checkValidity()) {
              input.target.classList.remove("is-invalid");
              input.target.classList.add("is-valid");
            } else {
              input.target.classList.remove("is-valid");
              input.target.classList.add("is-invalid");
            }
          }
        }

        function handleSaveClick(event) {
          event.preventDefault();

          if (transaksiKeluarForm.checkValidity() && barang != null) {
            //Disabled All Inputs and Show Loading
            transaksiKeluarModal.querySelectorAll("button").forEach((button) => button.disabled = true);
            transaksiKeluarForm.querySelectorAll("input").forEach((input) => input.disabled = true);
            buttonSave.querySelector(".spinner").classList.remove("d-none");

            //Checking Data
            const edit = modalData ? true : false;
            const data = edit ? JSON.parse(modalData) : {};

            //Add or Edit Transaksi Keluar
            fetch("/api/transaksi-keluar", {
              method: edit ? "PUT" : "POST",
              headers: {
                [csrfHeader]: csrfToken,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                id: edit ? data.id : "",
                hargaJual: hargaJual.value,
                beratKg: beratKg.value,
                barang: barang,
              })
            }).then((response) => {
              if (response.ok) return response.json();
            }).then((dataResponse) => {
              const search = getParam("cari");
              const page = getParam("page");

              //Reload Content
              fetch(edit ? `/transaksi-keluar?cari=${search}&page=${page}` : "/transaksi-keluar", {
                method: "POST",
                headers: { [csrfHeader]: csrfToken }
              }).then((response) => {
                if (response.ok) return response.text();
              }).then((html) => {
                content.innerHTML = html;

                //Clear Params If Not Editing
                if (!edit) deleteAllParams();

                //Reinitialize Functions
                transaksiKeluar();
                searchTransaksiKeluar();
                paginationTransaksiKeluar();

                //Show Effect Changed
                const targetID = dataResponse.id;
                const tableBody = content.querySelector("tbody");

                if (tableBody) {
                  tableBody.querySelectorAll("tr").forEach((row) => {
                    if (row.dataset.id === targetID) {
                      row.classList.add(edit ? "row-edit-data" : "row-add-data");
                      setTimeout(() => row.className = "", 1500);
                    }
                  });
                }
              });
              bootstrap.Modal.getInstance(transaksiKeluarModal).hide();
            });
          } else {
            if (barang == null) namaBarang.classList.add("is-invalid");
            if (beratKg.value < 1) beratKg.classList.add("is-invalid");
            if (hargaJual.value == "" || hargaJual.value < 0) hargaJual.classList.add("is-invalid");
          }
        }
      }

      searchTransaksiKeluar();
      function searchTransaksiKeluar() {
        //Initialize document
        const buttonSearchDate = content.querySelector(".search button[type='custom']");
        const buttonSearch = content.querySelector(".search button[type='button']");
        const textSearch = content.querySelector(".search input");

        //Params to Input Search
        textSearch.value = getParam("cari");

        //Show Date Picker
        flatpickr(buttonSearchDate, {
          locale: "id",
          dateFormat: "d/m/Y",
          onReady: (_, __, instance) => {
            const month = instance.monthsDropdownContainer;
            const year = instance.currentYearElement;

            const textMonthYear = document.createElement("div");
            textMonthYear.className = "custom-month-year";
            updateText();

            month.classList.add("d-none");
            year.parentElement.classList.add("d-none");
            month.parentElement.appendChild(textMonthYear);

            instance.config.onMonthChange.push(updateText);
            instance.config.onYearChange.push(updateText);

            function updateText() {
              const year = instance.currentYear;
              const month = instance.l10n.months.longhand[instance.currentMonth];
              textMonthYear.textContent = `${month} ${year}`;
            }
          },
          onChange: (_, selected, __) => {
            textSearch.value = selected;
            handleSearchClick();
          },
        });

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          //Get Content
          fetch(`/transaksi-keluar?cari=${textSearch.value}`, {
            method: "POST",
            headers: { [csrfHeader]: csrfToken }
          }).then((response) => {
            if (response.ok) return response.text();
          }).then((html) => {
            content.innerHTML = html;

            //Update Params
            deleteAllParams();
            addParam("cari", textSearch.value);

            //Reinitialize Functions
            transaksiKeluar();
            searchTransaksiKeluar();
            paginationTransaksiKeluar();
          });
        }
      }

      paginationTransaksiKeluar();
      function paginationTransaksiKeluar() {
        //Initialize document
        const pagination = content.querySelector(".pagination");

        //Check Pagination Displayed
        if (pagination) {
          //Reset Page Event
          pagination.removeEventListener("click", handlePageClick);
          pagination.addEventListener("click", handlePageClick);

          //Back or Forward Clicked
          window.addEventListener("popstate", () => location.reload());
        }

        function handlePageClick(event) {
          const target = event.target.closest(".page-item");
          const disabled = target.classList.contains("disabled");
          const active = target.classList.contains("active");
          const dots = target.classList.contains("dots");

          //Checked Not Disabled or Not Active or Not Dots
          if (!disabled && !active && !dots) {
            const pageLink = target.querySelector("a");
            const page = pageLink.dataset.page;
            const hasSearch = hasParam("cari");
            const search = getParam("cari");

            //Get Content
            fetch(hasSearch ? `/transaksi-keluar?cari=${search}&page=${page}` : `/transaksi-keluar?page=${page}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              //Add Param
              addParam("page", page);

              //Reinitialize Functions
              transaksiKeluar();
              searchTransaksiKeluar();
              paginationTransaksiKeluar();
            });
          }
        }
      }
    }

    //~~~~~ Rekapitulasi Laporan ~~~~~~~~~~~~~~~//
    if (currentUrl === "/rekapitulasi-laporan") {
      rekapitulasiLaporan();
      function rekapitulasiLaporan() {
        //Initialize document
        const rekapitulasi = document.getElementById("rekapitulasiLaporan");
        const jenisLaporan = rekapitulasi.querySelector("#jenisLaporan");
        const buttonSearch = rekapitulasi.querySelector("button[type='search']");
        const tableData = rekapitulasi.querySelector("table");

        let jenisLaporanValue = null;

        //Jenis Laporan Handle Select Change
        jenisLaporan.closest(".dropdown").querySelectorAll(".dropdown-menu .dropdown-item").forEach((item) => {
          if (item.dataset.value == getParam("laporan")) {
            jenisLaporanValue = item.dataset.value;
            jenisLaporan.textContent = item.textContent;
          }

          item.addEventListener("click", (event) => {
            jenisLaporanValue = event.target.dataset.value;
            jenisLaporan.textContent = event.target.textContent;
          });
        });

        //Reset Search Event
        buttonSearch.removeEventListener("click", handleSearchClick);
        buttonSearch.addEventListener("click", handleSearchClick);

        function handleSearchClick() {
          if (jenisLaporanValue != null) {
            //Get Content
            fetch(`/rekapitulasi-laporan?laporan=${jenisLaporanValue}`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.text();
            }).then((html) => {
              content.innerHTML = html;

              addParam("laporan", jenisLaporanValue);
              rekapitulasiLaporan();
            });
          }
        }

        //Table Data
        if (tableData) {
          const value = tableData.dataset.value;
          const data = JSON.parse(value);
          let total = 0;

          //Initialize Table Document
          const totalAll = tableData.parentElement.querySelector("h6");
          const exportPDF = tableData.parentElement.querySelector(".pdf");

          //Show Total All
          if (tableData.getAttribute("id") == "dataModal") {
            data.forEach((data) => total += data.totalModal);
          } else {
            data.forEach((data) => total += data.totalHarga);
          }
          totalAll.textContent = `${totalAll.textContent} : Rp${total.toLocaleString("ID-id")}`;

          //Export Laporan PDF
          exportPDF.addEventListener("click", () => {
            fetch(`/rekapitulasi-laporan?laporan=${jenisLaporanValue}&export=true`, {
              method: "POST",
              headers: { [csrfHeader]: csrfToken }
            }).then((response) => {
              if (response.ok) return response.blob();
            }).then((pdf) => {
              const url = URL.createObjectURL(pdf);
              const a = document.createElement("a");

              a.href = url;
              a.click();
            });
          });
        }
      }
    }
  }
});

function hasParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.has(param);
}

function getParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param) || "";
}

function addParam(param, value) {
  const locationSearch = window.location.search;
  const locationPathname = window.location.pathname;
  const urlParams = new URLSearchParams(locationSearch);

  urlParams.set(param, value);
  window.history.pushState({}, "", `${locationPathname}?${urlParams.toString()}`);
}

function deleteAllParams() {
  const locationPathname = window.location.pathname;
  window.history.pushState({}, "", locationPathname);
}