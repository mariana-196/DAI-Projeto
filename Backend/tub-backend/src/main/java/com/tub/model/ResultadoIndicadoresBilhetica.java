package com.tub.model;

import java.util.Map;

/**
 * Linha 79: DTO que consolida os indicadores de Bilhética (UC 5.2.3).
 */
public class ResultadoIndicadoresBilhetica {
    private int totalValidacoes;
    private Map<String, Integer> perfisPassageiros; // Ex: "Normal" -> 8500, "Estudante" -> 3200
    private String picoProcura;                     // Ex: "08:00 - 09:00"
    private double receitaEstimada;                 // Em Euros (€)

    public ResultadoIndicadoresBilhetica(int totalValidacoes, Map<String, Integer> perfisPassageiros, 
                                         String picoProcura, double receitaEstimada) {
        this.totalValidacoes = totalValidacoes;
        this.perfisPassageiros = perfisPassageiros;
        this.picoProcura = picoProcura;
        this.receitaEstimada = receitaEstimada;
    }

    // Getters para o Spring Boot conseguir converter para JSON
    public int getTotalValidacoes() { return totalValidacoes; }
    public Map<String, Integer> getPerfisPassageiros() { return perfisPassageiros; }
    public String getPicoProcura() { return picoProcura; }
    public double getReceitaEstimada() { return receitaEstimada; }
}
