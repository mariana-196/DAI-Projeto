package com.tub.p7_relatorios.service;

import org.springframework.stereotype.Service;

@Service
public class ControloAnonimizacao {

    public String anonimizarEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "utilizador_anonimizado";
        }

        String[] partes = email.split("@");
        String nome = partes[0];
        String dominio = partes[1];

        if (nome.length() <= 1) {
            return "*@" + dominio;
        }

        return nome.charAt(0) + "***@" + dominio;
    }
}