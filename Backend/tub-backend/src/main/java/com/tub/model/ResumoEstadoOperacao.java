package com.tub.model;

public class ResumoEstadoOperacao {
    private int servicosEmCurso;
    private int totalAtrasos;
    private int totalInterrupcoes;
    private String nivelGravidadeGeral;
    private int alertasCriticos;
    private int alertasModerados;
    private int alertasLigeiros;

    // Construtor completo para aceitar todos os parâmetros do Service
    public ResumoEstadoOperacao(int servicosEmCurso, int totalAtrasos, int totalInterrupcoes, 
                                int alertasCriticos, int alertasModerados, int alertasLigeiros, 
                                String nivelGravidadeGeral) {
        this.servicosEmCurso = servicosEmCurso;
        this.totalAtrasos = totalAtrasos;
        this.totalInterrupcoes = totalInterrupcoes;
        this.alertasCriticos = alertasCriticos;
        this.alertasModerados = alertasModerados;
        this.alertasLigeiros = alertasLigeiros;
        this.nivelGravidadeGeral = nivelGravidadeGeral;
    }

    // Getters (Essenciais para o Spring converter para JSON e enviar ao Frontend)
    public int getServicosEmCurso() { return servicosEmCurso; }
    public int getTotalAtrasos() { return totalAtrasos; }
    public int getTotalInterrupcoes() { return totalInterrupcoes; }
    public int getAlertasCriticos() { return alertasCriticos; }
    public int getAlertasModerados() { return alertasModerados; }
    public int getAlertasLigeiros() { return alertasLigeiros; }
    public String getNivelGravidadeGeral() { return nivelGravidadeGeral; }
}