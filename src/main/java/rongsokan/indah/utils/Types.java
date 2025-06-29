package rongsokan.indah.utils;

import java.math.BigDecimal;

public class Types {

    public static boolean isBigDecimal(String input) {
        try {
            new BigDecimal(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}