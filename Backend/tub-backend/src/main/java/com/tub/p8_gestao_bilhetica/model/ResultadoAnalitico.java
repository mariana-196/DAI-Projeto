package com.tub.p8_gestao_bilhetica.model;

public class ResultadoAnalitico {

    private double taxaOcupacaoMedia;
    private int totalPassageiros;

    public ResultadoAnalitico() {
    }

    public double getTaxaOcupacaoMedia() {
        return taxaOcupacaoMedia;
    }

    public void setTaxaOcupacaoMedia(double taxaOcupacaoMedia) {
        this.taxaOcupacaoMedia = taxaOcupacaoMedia;
    }

    public int getTotalPassageiros() {
        return totalPassageiros;
    }

    public void setTotalPassageiros(int totalPassageiros) {
        this.totalPassageiros = totalPassageiros;
    }
}
