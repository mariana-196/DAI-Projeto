package com.tub.model;

/**
 * Linha 76: DTO que representa os indicadores de desempenho da frota.
 */
public class ResultadoIndicadoresFrota {
    private double taxaDisponibilidade;
    private int viaturasAtivas;
    private int viaturasEmManutencao;
    private int viaturasSemComunicacao;
    private double consumoMedio;

    public ResultadoIndicadoresFrota(double taxaDisponibilidade, int viaturasAtivas, 
                                     int viaturasEmManutencao, int viaturasSemComunicacao, 
                                     double consumoMedio) {
        this.taxaDisponibilidade = taxaDisponibilidade;
        this.viaturasAtivas = viaturasAtivas;
        this.viaturasEmManutencao = viaturasEmManutencao;
        this.viaturasSemComunicacao = viaturasSemComunicacao;
        this.consumoMedio = consumoMedio;
    }

    // Getters
    public double getTaxaDisponibilidade() { return taxaDisponibilidade; }
    public int getViaturasAtivas() { return viaturasAtivas; }
    public int getViaturasEmManutencao() { return viaturasEmManutencao; }
    public int getViaturasSemComunicacao() { return viaturasSemComunicacao; }
    public double getConsumoMedio() { return consumoMedio; }
}