package com.tub.p7_relatorios.dto;

public class FicheiroPDF {

    private String nomeFicheiro;
    private String conteudoBase64;
    private String tipoConteudo;

    public FicheiroPDF() {
    }

    public FicheiroPDF(String nomeFicheiro, String conteudoBase64, String tipoConteudo) {
        this.nomeFicheiro = nomeFicheiro;
        this.conteudoBase64 = conteudoBase64;
        this.tipoConteudo = tipoConteudo;
    }

    public String getNomeFicheiro() {
        return nomeFicheiro;
    }

    public String getConteudoBase64() {
        return conteudoBase64;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }
}