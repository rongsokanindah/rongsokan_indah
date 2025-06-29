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

    //~~~~~ Modal ~~~~~~~~~~~~~~~//
    if (currentUrl === "/modal") {
      modal();
      function modal() {
        //Initialize document
        const modalModal = document.getElementById("modal");
        const modalForm = modalModal.querySelector("form");
        const namaAnakBuah = modalForm.querySelector("#namaAnakBuah");
        const jumlahModal = modalForm.querySelector("#jumlahModal");
        const buttonSave = modalModal.querySelector("button[type='submit']");

        //Reset Modal Event
        modalModal.removeEventListener("show.bs.modal", handleShowModal);
        modalModal.addEventListener("show.bs.modal", handleShowModal);

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
                deleteModal();
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

      deleteModal();
      function deleteModal() {
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
            const modal = JSON.parse(modalData);
            confirmModal.querySelector(".delete-data").textContent = modal.anakBuah.nama;
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

            //Delete Modal
            fetch(`/api/modal/${targetID}`, {
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
                      fetch(`/modal?cari=${search}&page=${page}`, {
                        method: "POST",
                        headers: { [csrfHeader]: csrfToken }
                      }).then((response) => {
                        if (response.ok) return response.text();
                      }).then((html) => {
                        content.innerHTML = html;

                        //Reinitialize Functions
                        modal();
                        deleteModal();
                        searchModal();
                        paginationModal();
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

      searchModal();
      function searchModal() {
        //Initialize document
        const buttonSearchDate = content.querySelector(".search button[type='calendar']");
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
            deleteModal();
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
              deleteModal();
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
        const barangForm = barangModal.querySelector("form");
        const namaBarang = barangForm.querySelector("#namaBarang");
        const hargaPerKg = barangForm.querySelector("#hargaPerKg");
        const buttonSave = barangModal.querySelector("button[type='submit']");

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