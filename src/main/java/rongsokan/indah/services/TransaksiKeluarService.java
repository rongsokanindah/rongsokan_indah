package rongsokan.indah.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import rongsokan.indah.entities.TransaksiKeluar;
import rongsokan.indah.repositories.TransaksiKeluarRepository;
import rongsokan.indah.utils.Dates;
import rongsokan.indah.utils.Types;

@Service
public class TransaksiKeluarService {

    @Autowired
    private TextConstant textConstant;

    @Autowired
    private TransaksiKeluarRepository transaksiKeluarRepository;

    public TransaksiKeluar saveTransaksiKeluar(TransaksiKeluar transaksiKeluar) {
        return transaksiKeluarRepository.save(transaksiKeluar);
    }

    public Page<TransaksiKeluar> getTransaksiKeluar(String cari, Pageable pageable) {
        if (cari.isEmpty()) {
            return transaksiKeluarRepository.findAll(pageable);
        }

        if (Types.isBigDecimal(cari)) {
            BigDecimal decimal = new BigDecimal(cari);
            return transaksiKeluarRepository.findByBeratKgOrHargaJual(decimal, decimal, pageable);
        }

        if (Dates.isLocalDate(cari)) {
            LocalDate date = Dates.parseDate(cari);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            return transaksiKeluarRepository.findByTanggalBetween(start, end, pageable);
        }

        return transaksiKeluarRepository.findByBarang_NamaBarangContainingIgnoreCase(cari, pageable);
    }

    public TransaksiKeluar updateTransaksiKeluar(TransaksiKeluar transaksiKeluar) {
        return transaksiKeluarRepository.findById(transaksiKeluar.getId()).map(data -> {
            Optional.ofNullable(transaksiKeluar.getBarang()).ifPresent(data::setBarang);
            Optional.ofNullable(transaksiKeluar.getBeratKg()).ifPresent(data::setBeratKg);
            Optional.ofNullable(transaksiKeluar.getHargaJual()).ifPresent(data::setHargaJual);

            return transaksiKeluarRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }

    public List<Map<String, Object>> getRekapitulasiLaporan() {
        return transaksiKeluarRepository.findAll().stream().collect(Collectors
            .groupingBy(TransaksiKeluar::getBarang)).entrySet().stream().map(entry -> {
                Barang barang = entry.getKey();
                List<TransaksiKeluar> list = entry.getValue();

                BigDecimal totalBerat = list.stream()
                    .map(TransaksiKeluar::getBeratKg)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalHarga = list.stream()
                    .map(TransaksiKeluar::getHargaJual)
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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String[] headerValue = {textConstant.getNo(), textConstant.getNamaBarang(), textConstant.getTotalBeratKg(), textConstant.getTotalHarga()};

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + textConstant.getTitle() + ".pdf");

            //Initialize
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
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
            titleReportParagraph.add(textConstant.getTransaksiKeluar());
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
            signitureParagraph.setSpacingBefore(20f);
            signitureParagraph.setAlignment(Element.ALIGN_RIGHT);
            signitureParagraph.setFont(addressFont);

            signitureParagraph.add(textConstant.getCity() + ", " + LocalDate.now().format(dateFormatter));
            signitureParagraph.add("\n\n\n" + textConstant.getTitle());
            document.add(signitureParagraph);

            document.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}