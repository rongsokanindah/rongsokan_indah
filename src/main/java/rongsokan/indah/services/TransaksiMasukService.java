    package rongsokan.indah.services;

    import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import rongsokan.indah.constants.TextConstant;
import rongsokan.indah.entities.Barang;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.entities.TransaksiMasuk;
import rongsokan.indah.repositories.BarangRepository;
import rongsokan.indah.repositories.PenggunaRepository;
import rongsokan.indah.repositories.TransaksiMasukRepository;
import rongsokan.indah.utils.Dates;
import rongsokan.indah.utils.FooterEvent;
import rongsokan.indah.utils.Types;

    @Service
    public class TransaksiMasukService {

        @Autowired
        private TextConstant textConstant;

        @Autowired
        private BarangRepository barangRepository;

        @Autowired
        private PenggunaRepository penggunaRepository;

        @Autowired
        private TransaksiMasukRepository transaksiMasukRepository;

        public TransaksiMasuk saveTransaksiMasuk(TransaksiMasuk transaksiMasuk) {
            return transaksiMasukRepository.save(transaksiMasuk);
        }

        public Page<TransaksiMasuk> getTransaksiMasuk(String cari, Pageable pageable) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
            boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

            if (cari.isEmpty()) {
                return isAdmin
                        ? transaksiMasukRepository.findAll(pageable)
                        : transaksiMasukRepository.findByAnakBuah_Id(pengguna.getAnakBuah().getId(), pageable);
            }

            if (Types.isBigDecimal(cari)) {
                BigDecimal decimal = new BigDecimal(cari);

                return isAdmin
                        ? transaksiMasukRepository.findByBeratKgOrTotalHarga(decimal, decimal, pageable)
                        : transaksiMasukRepository.findByAnakBuah_IdAndBeratKgOrTotalHarga(pengguna.getAnakBuah().getId(), decimal, decimal, pageable);
            }

            if (Dates.isLocalDate(cari)) {
                LocalDate date = Dates.parseDate(cari);
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.atTime(LocalTime.MAX);

                return isAdmin
                        ? transaksiMasukRepository.findByTanggalBetween(start, end, pageable)
                        : transaksiMasukRepository.findByAnakBuah_IdAndTanggalBetween(pengguna.getAnakBuah().getId(), start, end, pageable);
            }

            return isAdmin
                    ? transaksiMasukRepository.findByAnakBuah_NamaContainingIgnoreCaseOrBarang_NamaBarangContainingIgnoreCase(cari, cari, pageable)
                    : transaksiMasukRepository.findByAnakBuah_IdAndBarang_NamaBarangContainingIgnoreCase(pengguna.getAnakBuah().getId(), cari, pageable);
        }

        public TransaksiMasuk updateTransaksiMasuk(TransaksiMasuk transaksiMasuk) {
            return transaksiMasukRepository.findById(transaksiMasuk.getId()).map(data -> {
                Optional.ofNullable(transaksiMasuk.getBarang()).ifPresent(data::setBarang);
                Optional.ofNullable(transaksiMasuk.getBeratKg()).ifPresent(data::setBeratKg);
                Optional.ofNullable(transaksiMasuk.getAnakBuah()).ifPresent(data::setAnakBuah);
                Optional.ofNullable(transaksiMasuk.getTotalHarga()).ifPresent(data::setTotalHarga);

                return transaksiMasukRepository.save(data);
            }).orElseThrow(() -> new EntityNotFoundException(""));
        }

        public List<Map<String, Object>> getRekapitulasiLaporan() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
            boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

            List<TransaksiMasuk> transaksiMasukList = isAdmin
                ? transaksiMasukRepository.findAll()
                : transaksiMasukRepository.findByAnakBuah_Id(pengguna.getAnakBuah().getId());

            return transaksiMasukList.stream().collect(Collectors
                .groupingBy(TransaksiMasuk::getBarang)).entrySet().stream().map(entry -> {
                    Barang barang = entry.getKey();
                    List<TransaksiMasuk> list = entry.getValue();

                    BigDecimal totalBerat = list.stream()
                        .map(TransaksiMasuk::getBeratKg)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalHarga = list.stream()
                        .map(TransaksiMasuk::getTotalHarga)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return Map.of(
                        "barang", barang,
                        "totalBerat", totalBerat,
                        "totalHarga", totalHarga
                    );
                }).collect(Collectors.toList());
        }

        public void generatePDF(HttpServletResponse response) {
            List<Map<String, Object>> data = getRekapitulasiLaporan();

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
                String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String[] headerValue = {textConstant.getNo(), textConstant.getNamaBarang(), textConstant.getTotalBeratKg(), textConstant.getTotalHarga()};

                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + textConstant.getTitle() + ".pdf");

                //Initialize
                Document document = new Document(PageSize.A4);
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
                writer.setPageEvent(new FooterEvent());
                document.open();

                //Header PDF
                PdfPTable headerPDF = new PdfPTable(2);
                headerPDF.setWidthPercentage(100);
                headerPDF.setWidths(new float[]{1, 4});

                //Logo
                ClassPathResource imageFile = new ClassPathResource("static/img/logo.png");
                Image logo = Image.getInstance(imageFile.getURL());
                logo.scaleToFit(75, 75);

                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setBorder(Rectangle.NO_BORDER);
                headerPDF.addCell(logoCell);

                //Title and Address
                Paragraph headerParagraph = new Paragraph();
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17, BaseColor.BLACK);
                Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

                headerParagraph.add(new Phrase(textConstant.getTitle().toUpperCase(), titleFont));
                headerParagraph.add(new Phrase("\n\n" + textConstant.getAddress(), addressFont));

                PdfPCell titleAddressCell = new PdfPCell(headerParagraph);
                titleAddressCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleAddressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                titleAddressCell.setBorder(Rectangle.NO_BORDER);
                headerPDF.addCell(titleAddressCell);
                document.add(headerPDF);

                //Line Separator
                LineSeparator separator = new LineSeparator();
                separator.setOffset(-2);
                separator.setLineWidth(1.3F);
                separator.setLineColor(BaseColor.BLACK);
                document.add(new Chunk(separator));

                //Add Title Report
                Font titleReportFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BaseColor.BLACK);
                Paragraph titleReportParagraph = new Paragraph();
                titleReportParagraph.setFont(titleReportFont);

                titleReportParagraph.add(textConstant.getRekapitulasiLaporan() + " ");
                titleReportParagraph.add(textConstant.getDataTransaksiMasuk());
                titleReportParagraph.setAlignment(Element.ALIGN_CENTER);
                titleReportParagraph.setSpacingBefore(30);
                titleReportParagraph.setSpacingAfter(10);
                document.add(titleReportParagraph);

                //Add Date Export
                Font dateFontPdf = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK);
                Paragraph dateParagraph = new Paragraph(currentDate, dateFontPdf);
                dateParagraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(dateParagraph);

                //Header Table
                PdfPTable table = new PdfPTable(headerValue.length);
                float[] columnWidth = {0.5f, 2f, 2f, 2f};

                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.setSpacingAfter(5);
                table.setWidths(columnWidth);

                Font headerFontPdf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
                for(String header : headerValue) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFontPdf));
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setPadding(3);
                    table.addCell(headerCell);
                }

                //Body Table
                BigDecimal totalBeratAll = BigDecimal.ZERO;
                BigDecimal totalHargaAll = BigDecimal.ZERO;

                Font dataFontPdf = FontFactory.getFont(FontFactory.HELVETICA, 9);
                for (int i = 0; i < data.size(); i++) {
                    Map<String, Object> row = data.get(i);
                    Barang barang = (Barang) row.get("barang");
                    BigDecimal totalBerat = (BigDecimal) row.get("totalBerat");
                    BigDecimal totalHarga = (BigDecimal) row.get("totalHarga");

                    PdfPCell numberPdfPCell = new PdfPCell(new Phrase(String.valueOf(i + 1), dataFontPdf));
                    numberPdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    numberPdfPCell.setPadding(3);
                    table.addCell(numberPdfPCell);

                    PdfPCell namaBarangCell = new PdfPCell(new Phrase(barang.getNamaBarang(), dataFontPdf));
                    namaBarangCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    namaBarangCell.setPadding(3);
                    table.addCell(namaBarangCell);

                    PdfPCell totalBeratCell = new PdfPCell(new Phrase(String.valueOf(totalBerat), dataFontPdf));
                    totalBeratCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    totalBeratCell.setPadding(3);
                    table.addCell(totalBeratCell);

                    String formattedPrice = numberFormat.format(totalHarga);
                    totalHargaAll = totalHargaAll.add(totalHarga);
                    totalBeratAll = totalBeratAll.add(totalBerat);

                    PdfPCell totalHargaCell = new PdfPCell(new Phrase(formattedPrice, dataFontPdf));
                    totalHargaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalHargaCell.setPadding(3);
                    table.addCell(totalHargaCell);
                }

                //Total All
                PdfPCell totalCell = new PdfPCell(new Phrase(textConstant.getTotal(), headerFontPdf));
                totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                totalCell.setColspan(2);
                table.addCell(totalCell);

                PdfPCell totalBeratAllCell = new PdfPCell(new Phrase(String.valueOf(totalBeratAll), headerFontPdf));
                totalBeratAllCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(totalBeratAllCell);

                PdfPCell totalHargaAllCell = new PdfPCell(new Phrase(numberFormat.format(totalHargaAll), headerFontPdf));
                totalHargaAllCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalHargaAllCell);
                document.add(table);

                //Signiture
                Paragraph signitureParagraph = new Paragraph();
                signitureParagraph.setSpacingBefore(50f);
                signitureParagraph.setAlignment(Element.ALIGN_LEFT);
                signitureParagraph.setFont(addressFont);

                signitureParagraph.add(textConstant.getCity() + ", " + LocalDate.now().format(dateFormatter));
                signitureParagraph.add("\n\n\n" + textConstant.getTitle());
                document.add(signitureParagraph);

                document.close();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }

        public Map<String, Object> getDashboard() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
            boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

            List<TransaksiMasuk> transaksiMasukList = isAdmin
                ? transaksiMasukRepository.findAll()
                : transaksiMasukRepository.findByAnakBuah_Id(pengguna.getAnakBuah().getId());

            return transaksiMasukList.stream().collect(Collectors.collectingAndThen(
                Collectors.toList(), list -> {
                    BigDecimal totalBerat = list.stream()
                        .map(TransaksiMasuk::getBeratKg)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalHarga = list.stream()
                        .map(TransaksiMasuk::getTotalHarga)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return Map.of(
                        "totalBerat", totalBerat,
                        "totalHarga", totalHarga
                    );
                }
            ));
        }

        public List<Map<String, Object>> saw() {
            //1. Group By Barang
            Map<Barang, List<TransaksiMasuk>> groupBarang = transaksiMasukRepository.findAll()
                .stream().collect(Collectors.groupingBy(TransaksiMasuk::getBarang));

            //2. Count Value per Barang
            Map<Barang, Map<String, Double>> valueMap = new HashMap<>();
            for (Map.Entry<Barang, List<TransaksiMasuk>> entry : groupBarang.entrySet()) {
                Barang barang = entry.getKey();
                List<TransaksiMasuk> transaksi = entry.getValue();

                double totalBerat = transaksi.stream()
                    .mapToDouble(t -> t.getBeratKg().doubleValue())
                    .sum();

                double hargaAverage = transaksi.stream()
                    .filter(t -> t.getBeratKg().doubleValue() > 0)
                    .mapToDouble(t -> t.getTotalHarga().doubleValue() / t.getBeratKg().doubleValue())
                    .average().orElse(0);

                int totalTransaksi = transaksi.size();

                Map<String, Double> value = new HashMap<>();
                value.put("harga", hargaAverage);
                value.put("volume", totalBerat);
                value.put("permintaan", (double) totalTransaksi);

                valueMap.put(barang, value);
            }

            //3. Normalisation
            double maxHarga = valueMap.values().stream().mapToDouble(m -> m.get("harga")).max().orElse(1);
            double maxVolume = valueMap.values().stream().mapToDouble(m -> m.get("volume")).max().orElse(1);
            double maxPermintaan = valueMap.values().stream().mapToDouble(m -> m.get("permintaan")).max().orElse(1);

            //4. Count Score Based On Document
            double weightHarga = 0.5;
            double weightVolume = 0.3;
            double weightPermintaan = 0.2;

            Map<Barang, Double> resultScore = new HashMap<>();
            for (Map.Entry<Barang, Map<String, Double>> entry : valueMap.entrySet()) {
                Barang barang = entry.getKey();
                Map<String, Double> value = entry.getValue();

                double valueHarga = value.get("harga") / maxHarga;
                double valueVolume = value.get("volume") / maxVolume;
                double valuePermintaan = value.get("permintaan") / maxPermintaan;

                double skor = (valueHarga * weightHarga) + (valueVolume * weightVolume) + (valuePermintaan * weightPermintaan);
                resultScore.put(barang, skor);
            }

            //5. Sort Result Score Based on Score Descending
            return barangRepository.findAll().stream().map(barang -> {
                Map<String, Object> map = new HashMap<>();
                map.put("barang", barang.getNamaBarang());
                map.put("skor", resultScore.getOrDefault(barang, 0.0));
                return map;
            }).sorted((a, b) -> {
                return Double.compare((Double) b.get("skor"), (Double) a.get("skor"));
            }).collect(Collectors.toList());
        }





        public Map<String, Object> sawMatriksLengkap() {
            // 1. Grouping transaksi berdasarkan barang
            Map<Barang, List<TransaksiMasuk>> groupBarang = transaksiMasukRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(TransaksiMasuk::getBarang));

            // 2. Matriks Keputusan
            Map<Barang, Map<String, Double>> matriksKeputusan = new LinkedHashMap<>();
            for (Map.Entry<Barang, List<TransaksiMasuk>> entry : groupBarang.entrySet()) {
                Barang barang = entry.getKey();
                List<TransaksiMasuk> transaksi = entry.getValue();

                double totalBerat = transaksi.stream()
                    .mapToDouble(t -> t.getBeratKg() != null ? t.getBeratKg().doubleValue() : 0.0)
                    .sum();

                double hargaAverage = transaksi.stream()
                    .filter(t -> t.getBeratKg() != null && t.getBeratKg().doubleValue() > 0)
                    .mapToDouble(t -> {
                        double berat = t.getBeratKg().doubleValue();
                        double harga = t.getTotalHarga() != null ? t.getTotalHarga().doubleValue() : 0.0;
                        return berat > 0 ? harga / berat : 0.0;
                    })
                    .average()
                    .orElse(0.0);

                int totalTransaksi = transaksi.size();

                Map<String, Double> value = new HashMap<>();
                value.put("harga", hargaAverage);
                value.put("volume", totalBerat);
                value.put("permintaan", (double) totalTransaksi);
                matriksKeputusan.put(barang, value);
            }

            // 3. Matriks Normalisasi
            double maxHarga = matriksKeputusan.values().stream()
                .mapToDouble(v -> v.getOrDefault("harga", 0.0))
                .max()
                .orElse(1);

            double maxVolume = matriksKeputusan.values().stream()
                .mapToDouble(v -> v.getOrDefault("volume", 0.0))
                .max()
                .orElse(1);

            double maxPermintaan = matriksKeputusan.values().stream()
                .mapToDouble(v -> v.getOrDefault("permintaan", 0.0))
                .max()
                .orElse(1);

            Map<Barang, Map<String, Double>> matriksNormalisasi = new LinkedHashMap<>();
            for (Map.Entry<Barang, Map<String, Double>> entry : matriksKeputusan.entrySet()) {
                Barang barang = entry.getKey();
                Map<String, Double> v = entry.getValue();

                Map<String, Double> norm = new HashMap<>();
                norm.put("harga", v.getOrDefault("harga", 0.0) / maxHarga);
                norm.put("volume", v.getOrDefault("volume", 0.0) / maxVolume);
                norm.put("permintaan", v.getOrDefault("permintaan", 0.0) / maxPermintaan);

                matriksNormalisasi.put(barang, norm);
            }

            // 4. Skor Akhir (dengan bobot C1, C2, C3)
            double weightHarga = 0.5, weightVolume = 0.3, weightPermintaan = 0.2;
            Map<Barang, Double> skorAkhir = new HashMap<>();
            for (Map.Entry<Barang, Map<String, Double>> entry : matriksNormalisasi.entrySet()) {
                Map<String, Double> n = entry.getValue();
                double skor = (n.getOrDefault("harga", 0.0) * weightHarga) +
                              (n.getOrDefault("volume", 0.0) * weightVolume) +
                              (n.getOrDefault("permintaan", 0.0) * weightPermintaan);
                skorAkhir.put(entry.getKey(), skor);
            }

            // 5. Format skor akhir sebagai list
            List<Map<String, Object>> skorList = skorAkhir.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("barang", e.getKey().getNamaBarang());
                    m.put("skor", e.getValue());
                    return m;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("skor"), (Double) a.get("skor")))
                .toList();

            // 6. Return semua matriks
            Map<String, Object> hasil = new HashMap<>();
            hasil.put("keputusan", matriksKeputusan);
            hasil.put("normalisasi", matriksNormalisasi);
            hasil.put("skor", skorList);
            return hasil;
        }

}