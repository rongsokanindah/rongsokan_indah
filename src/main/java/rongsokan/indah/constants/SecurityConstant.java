package rongsokan.indah.constants;

public class SecurityConstant {
    public static String[] permitAll() {
        return new String[] {
            "/", "/css/**", "/js/**", "/img/**", "/login",
            "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**"
        };
    }
}