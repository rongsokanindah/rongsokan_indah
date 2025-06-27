package rongsokan.indah.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Dates {
    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = List.of(
        DateTimeFormatter.ofPattern("yyyyMMdd"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("ddMMyyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    public static boolean isLocalDate(String input) {
        for (DateTimeFormatter format : SUPPORTED_FORMATS) {
            try {
                LocalDate.parse(input, format);
                return true;
            } catch (DateTimeParseException e) {}
        }
        return false;
    }
}