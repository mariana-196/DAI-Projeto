package com.tub.adapter;

import org.springframework.stereotype.Component;

@Component
public class AutenticacaoGovAdapter {

    public boolean autenticar(String email, String codigoGov) {
        if (email == null || email.isBlank()) {
            return false;
        }

        return "AUTHGOV_OK".equals(codigoGov);
    }
}
