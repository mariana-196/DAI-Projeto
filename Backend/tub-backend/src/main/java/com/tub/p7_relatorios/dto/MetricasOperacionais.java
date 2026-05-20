package com.tub.p7_relatorios.dto;

public class MetricasOperacionais {

    private String linha;
    private String veiculo;
    private String paragem;
    private String lotacaoMedia;
    private int passageirosTransportados;
    private String atrasoMedio;

    public MetricasOperacionais() {
    }

    public MetricasOperacionais(String linha, String veiculo, String paragem, String lotacaoMedia, int passageirosTransportados, String atrasoMedio) {
        this.linha = linha;
        this.veiculo = veiculo;
        this.paragem = paragem;
        this.lotacaoMedia = lotacaoMedia;
        this.passageirosTransportados = passageirosTransportados;
        this.atrasoMedio = atrasoMedio;
    }

    public String getLinha() {
        return linha;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public String getParagem() {
        return paragem;
    }

    public String getLotacaoMedia() {
        return lotacaoMedia;
    }

    public int getPassageirosTransportados() {
        return passageirosTransportados;
    }

    public String getAtrasoMedio() {
        return atrasoMedio;
    }
}