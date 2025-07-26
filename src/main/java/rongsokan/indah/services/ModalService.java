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
import rongsokan.indah.entities.AnakBuah;
import rongsokan.indah.entities.Modal;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.repositories.ModalRepository;
import rongsokan.indah.repositories.PenggunaRepository;
import rongsokan.indah.utils.Dates;
import rongsokan.indah.utils.Types;

@Service
public class ModalService {

    @Autowired
    private TextConstant textConstant;

    @Autowired
    private ModalRepository modalRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    public Modal saveModal(Modal modal) {
        return modalRepository.save(modal);
    }

    public Page<Modal> getModal(String cari, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
        boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

        if (cari.isEmpty()) {
            return isAdmin
                    ? modalRepository.findAll(pageable)
                    : modalRepository.findByAnakBuah_Id(pengguna.getAnakBuah().getId(), pageable);
        }

        if (Types.isBigDecimal(cari)) {
            BigDecimal jumlah = new BigDecimal(cari);

            return isAdmin
                    ? modalRepository.findByJumlah(jumlah, pageable)
                    : modalRepository.findByAnakBuah_IdAndJumlah(pengguna.getAnakBuah().getId(), jumlah, pageable);
        }

        if (Dates.isLocalDate(cari)) {
            LocalDate date = Dates.parseDate(cari);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            return isAdmin
                    ? modalRepository.findByTanggalBetween(start, end, pageable)
                    : modalRepository.findByAnakBuah_IdAndTanggalBetween(pengguna.getAnakBuah().getId(), start, end, pageable);
        }

        return isAdmin
                ? modalRepository.findByAnakBuah_NamaContainingIgnoreCase(cari, pageable)
                : modalRepository.findByAnakBuah_IdAndAnakBuah_NamaContainingIgnoreCase(pengguna.getAnakBuah().getId(), cari, pageable);
    }

    public Modal updateModal(Modal modal) {
        return modalRepository.findById(modal.getId()).map(data -> {
            Optional.ofNullable(modal.getAnakBuah()).ifPresent(data::setAnakBuah);
            Optional.ofNullable(modal.getJumlah()).ifPresent(data::setJumlah);

            return modalRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }

    public List<Map<String, Object>> getRekapitulasiLaporan() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
        boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

        List<Modal> modalList = isAdmin
            ? modalRepository.findAll()
            : modalRepository.findByAnakBuah_Id(pengguna.getAnakBuah().getId());

        return modalList.stream().collect(Collectors.groupingBy(
            modal -> modal.getAnakBuah(),
            Collectors.reducing(BigDecimal.ZERO, Modal::getJumlah, BigDecimal::add)
        )).entrySet().stream().map(modal -> Map.of(
            "anakBuah", modal.getKey(),
            "totalModal", modal.getValue()
        )).toList();
    }

    public void generatePDF(HttpServletResponse response) {
        List<Map<String, Object>> data = getRekapitulasiLaporan();

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String[] headerValue = {textConstant.getNo(), textConstant.getNamaAnakBuah(), textConstant.getTotalModal()};

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
            titleReportParagraph.add(textConstant.getDataModal());
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
            float[] columnWidth = {0.5f, 2f, 2f};

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
            BigDecimal totalAll = BigDecimal.ZERO;
            Font dataFontPdf = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> row = data.get(i);
                AnakBuah anakBuah = (AnakBuah) row.get("anakBuah");
                BigDecimal totalModal = (BigDecimal) row.get("totalModal");

                PdfPCell numberPdfPCell = new PdfPCell(new Phrase(String.valueOf(i + 1), dataFontPdf));
                numberPdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                numberPdfPCell.setPadding(3);
                table.addCell(numberPdfPCell);

                PdfPCell namaAnakBuahCell = new PdfPCell(new Phrase(anakBuah.getNama(), dataFontPdf));
                namaAnakBuahCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                namaAnakBuahCell.setPadding(3);
                table.addCell(namaAnakBuahCell);

                String formattedPrice = numberFormat.format(totalModal);
                totalAll = totalAll.add(totalModal);

                PdfPCell totalModalCell = new PdfPCell(new Phrase(formattedPrice, dataFontPdf));
                totalModalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalModalCell.setPadding(3);
                table.addCell(totalModalCell);
            }

            //Total All
            PdfPCell totalCell = new PdfPCell(new Phrase(textConstant.getTotal(), headerFontPdf));
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalCell.setColspan(2);
            table.addCell(totalCell);

            PdfPCell totalAllCell = new PdfPCell(new Phrase(numberFormat.format(totalAll), headerFontPdf));
            totalAllCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalAllCell);
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