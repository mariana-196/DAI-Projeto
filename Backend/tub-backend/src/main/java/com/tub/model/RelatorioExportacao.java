package com.tub.model;

import java.time.LocalDateTime;
import java.util.Map;

public class RelatorioExportacao {
    
    private String titulo;
    private LocalDateTime dataGeracao;
    private Map<String, Object> metricas;

    public RelatorioExportacao() {
        // O construtor vazio é necessário
    }

    // --- Getters e Setters ---
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Map<String, Object> getMetricas() {
        return metricas;
    }

    public void setMetricas(Map<String, Object> metricas) {
        this.metricas = metricas;
    }
}