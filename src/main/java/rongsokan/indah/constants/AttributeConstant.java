package rongsokan.indah.constants;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class AttributeConstant {
    private final String loginError = "LOGIN_ERROR";
    private final String anakBuah = "dataAnakBuah";
    private final String pengguna = "pengguna";
    private final String barang = "dataBarang";
    private final String modal = "dataModal";
    private final String akun = "dataAkun";
    private final String path = "path";
}