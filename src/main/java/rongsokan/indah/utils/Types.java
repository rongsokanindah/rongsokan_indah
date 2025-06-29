package rongsokan.indah.utils;

import java.math.BigDecimal;

import rongsokan.indah.entities.Pengguna.Role;

public class Types {

    public static boolean isBigDecimal(String input) {
        try {
            new BigDecimal(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidRole(String input) {
        for (Role role : Role.values()) {
            if (role.name().equals(input)) {
                return true;
            }
        }
        return false;
    }
}