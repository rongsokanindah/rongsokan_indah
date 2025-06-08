package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import rongsokan.indah.constants.AttributeConstant;
import rongsokan.indah.entities.Barang;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.repositories.BarangRepository;
import rongsokan.indah.repositories.PenggunaRepository;

@Service
public class PageService {

    @Autowired
    private AttributeConstant attribute;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private BarangRepository barangRepository;

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

    public String getBarangPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getBarang(), dataBarang(cari, pageable));
        model.addAttribute(attribute.getPath(), "/barang");
        return "pages/dashboard";
    }

    public String postBarangPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getBarang(), dataBarang(cari, pageable));
        return "fragments/barang::reload";
    }

    public Page<Barang> dataBarang(String cari, Pageable pageable) {
        if (cari.isEmpty()) {
            return barangRepository.findAll(pageable);
        } else {
            return barangRepository.findByNamaBarangContainingIgnoreCase(cari, pageable);
        }
    }
}