package rongsokan.indah.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class FooterEvent extends PdfPageEventHelper {

    Font font = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
        PdfPTable footer = new PdfPTable(1);
        try {
            footer.setTotalWidth(500);
            footer.setWidths(new int[] { 1 });
            footer.setLockedWidth(true);
            footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            footer.addCell(new Phrase("Tanggal cetak: " + LocalDateTime.now().format(dateFormatter), font));

            // posisi footer (X, Y)
            footer.writeSelectedRows(0, -1,
                    (document.right() - document.left() - 500) / 2 + document.leftMargin(),
                    document.bottom() - 10, writer.getDirectContent());
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}