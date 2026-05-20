package com.tub.p7_relatorios.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DadosRelatorio {

    private String titulo;
    private String tipo;
    private LocalDateTime geradoEm;
    private String geradoPor;
    private List<Map<String, Object>> dados;

    public DadosRelatorio() {
    }

    public DadosRelatorio(String titulo, String tipo, LocalDateTime geradoEm, String geradoPor, List<Map<String, Object>> dados) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.geradoEm = geradoEm;
        this.geradoPor = geradoPor;
        this.dados = dados;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getGeradoEm() {
        return geradoEm;
    }

    public String getGeradoPor() {
        return geradoPor;
    }

    public List<Map<String, Object>> getDados() {
        return dados;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setGeradoEm(LocalDateTime geradoEm) {
        this.geradoEm = geradoEm;
    }

    public void setGeradoPor(String geradoPor) {
        this.geradoPor = geradoPor;
    }

    public void setDados(List<Map<String, Object>> dados) {
        this.dados = dados;
    }
}