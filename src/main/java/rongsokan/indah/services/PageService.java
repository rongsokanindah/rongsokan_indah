package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import rongsokan.indah.constants.AttributeConstant;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.repositories.PenggunaRepository;

@Service
public class PageService {

    @Autowired
    private AttributeConstant attribute;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PenggunaService penggunaService;

    @Autowired
    private ModalService modalService;

    @Autowired
    private BarangService barangService;

    @Autowired
    private AnakBuahService anakBuahService;

    public String getLoginPage(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        } else {
            String loginError = (String) request.getSession().getAttribute(attribute.getLoginError());

            if (loginError != null) {
                model.addAttribute(attribute.getLoginError(), loginError);
                request.getSession().removeAttribute(attribute.getLoginError());
            }
            return "pages/login";
        }
    }

    public void getDataLogin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();

            model.addAttribute(attribute.getPengguna(), pengguna);
        }
    }

    public String getKelolaAkunPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getAkun(), penggunaService.getPengguna(cari, pageable));
        model.addAttribute(attribute.getPath(), "/kelola-akun");
        return "pages/dashboard";
    }

    public String postKelolaAkunPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getAkun(), penggunaService.getPengguna(cari, pageable));
        return "fragments/kelola-akun::reload";
    }

    public String getModalPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getModal(), modalService.getModal(cari, pageable));
        model.addAttribute(attribute.getPath(), "/modal");
        return "pages/dashboard";
    }

    public String postModalPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getModal(), modalService.getModal(cari, pageable));
        return "fragments/modal::reload";
    }

    public String getBarangPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getBarang(), barangService.getBarang(cari, pageable));
        model.addAttribute(attribute.getPath(), "/barang");
        return "pages/dashboard";
    }

    public String postBarangPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getBarang(), barangService.getBarang(cari, pageable));
        return "fragments/barang::reload";
    }

    public String getAnakBuahPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getAnakBuah(), anakBuahService.getAnakBuah(cari, pageable));
        model.addAttribute(attribute.getPath(), "/anak-buah");
        return "pages/dashboard";
    }

    public String postAnakBuahPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getAnakBuah(), anakBuahService.getAnakBuah(cari, pageable));
        return "fragments/anak-buah::reload";
    }
}