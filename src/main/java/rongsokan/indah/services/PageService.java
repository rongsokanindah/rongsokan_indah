package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private TransaksiMasukService transaksiMasukService;

    @Autowired
    private TransaksiKeluarService transaksiKeluarService;

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
        model.addAttribute(attribute.getAkun(), penggunaService.getPengguna(cari, "", pageable));
        model.addAttribute(attribute.getPath(), "/kelola-akun");
        return "pages/dashboard";
    }

    public String postKelolaAkunPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getAkun(), penggunaService.getPengguna(cari, "", pageable));
        return "fragments/kelola-akun::reload";
    }

    public String getProfilPage(Model model) {
        model.addAttribute(attribute.getPath(), "/profil");
        return "pages/dashboard";
    }

    public String getDashboardPage(Model model) {
        model.addAttribute(attribute.getTransaksiKeluar(), transaksiKeluarService.getDashboard());
        model.addAttribute(attribute.getTransaksiMasuk(), transaksiMasukService.getDashboard());
        model.addAttribute(attribute.getSawTransaksiKeluar(), transaksiKeluarService.saw());
        model.addAttribute(attribute.getSawTransaksiMasuk(), transaksiMasukService.saw());
        model.addAttribute(attribute.getAnakBuah(), anakBuahService.getDashboard());
        model.addAttribute(attribute.getModal(), modalService.getDashboard());
        model.addAttribute(attribute.getPath(), "/dashboard");


        model.addAttribute("sawTransaksiMasukFull", transaksiMasukService.sawMatriksLengkap());
        model.addAttribute("sawTransaksiKeluarFull", transaksiKeluarService.sawMatriksLengkap());

        return "pages/dashboard";
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

    public String getTransaksiMasukPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getTransaksiMasuk(), transaksiMasukService.getTransaksiMasuk(cari, pageable));
        model.addAttribute(attribute.getPath(), "/transaksi-masuk");
        return "pages/dashboard";
    }

    public String postTransaksiMasukPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getTransaksiMasuk(), transaksiMasukService.getTransaksiMasuk(cari, pageable));
        return "fragments/transaksi-masuk::reload";
    }

    public String getTransaksiKeluarPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getTransaksiKeluar(), transaksiKeluarService.getTransaksiKeluar(cari, pageable));
        model.addAttribute(attribute.getPath(), "/transaksi-keluar");
        return "pages/dashboard";
    }

    public String postTransaksiKeluarPage(Model model, String cari, Pageable pageable) {
        model.addAttribute(attribute.getTransaksiKeluar(), transaksiKeluarService.getTransaksiKeluar(cari, pageable));
        return "fragments/transaksi-keluar::reload";
    }

    public String getRekapitulasiLaporanPage(Model model, String laporan) {
        model.addAttribute(attribute.getPath(), "/rekapitulasi-laporan");
        if (laporan.equals(attribute.getModal())) {
            model.addAttribute(attribute.getModal(), modalService.getRekapitulasiLaporan());
        } else if (laporan.equals(attribute.getTransaksiMasuk())) {
            model.addAttribute(attribute.getTransaksiMasuk(), transaksiMasukService.getRekapitulasiLaporan());
        } else if (laporan.equals(attribute.getTransaksiKeluar())) {
            model.addAttribute(attribute.getTransaksiKeluar(), transaksiKeluarService.getRekapitulasiLaporan());
        }
        return "pages/dashboard";
    }

    public Object postRekapitulasiLaporanPage(Model model, HttpServletResponse response, boolean export, String laporan) {
        if (laporan.equals(attribute.getModal())) {
            model.addAttribute(attribute.getModal(), modalService.getRekapitulasiLaporan());

            if (export) {
                modalService.generatePDF(response);
                return ResponseEntity.ok().build();
            }
        } else if (laporan.equals(attribute.getTransaksiMasuk())) {
            model.addAttribute(attribute.getTransaksiMasuk(), transaksiMasukService.getRekapitulasiLaporan());

            if (export) {
                transaksiMasukService.generatePDF(response);
                return ResponseEntity.ok().build();
            }
        } else if (laporan.equals(attribute.getTransaksiKeluar())) {
            model.addAttribute(attribute.getTransaksiKeluar(), transaksiKeluarService.getRekapitulasiLaporan());

            if (export) {
                transaksiKeluarService.generatePDF(response);
                return ResponseEntity.ok().build();
            }
        }
        return "fragments/rekapitulasi-laporan::reload";
    }







    public String getPerhitunganSAW(Model model) {
        model.addAttribute(attribute.getPath(), "/perhitungan-saw");

        model.addAttribute("sawTransaksiMasukFull", transaksiMasukService.sawMatriksLengkap());
        model.addAttribute("sawTransaksiKeluarFull", transaksiKeluarService.sawMatriksLengkap());

        return "pages/dashboard";
    }
}