package com.tub.p7_relatorios.dto;

import java.time.LocalDate;

public class RelatorioFiltroDTO {

    private String tipoRelatorio; // AUDITORIA ou OPERACIONAL
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // filtros do relatório de auditoria
    private String utilizador;
    private String severidade;
    private String evento;

    // filtros do relatório operacional
    private String linha;
    private String veiculo;
    private String paragem;

    // comparação de períodos
    private LocalDate periodoComparacaoInicio;
    private LocalDate periodoComparacaoFim;

    public RelatorioFiltroDTO() {
    }

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getUtilizador() {
        return utilizador;
    }

    public void setUtilizador(String utilizador) {
        this.utilizador = utilizador;
    }

    public String getSeveridade() {
        return severidade;
    }

    public void setSeveridade(String severidade) {
        this.severidade = severidade;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getParagem() {
        return paragem;
    }

    public void setParagem(String paragem) {
        this.paragem = paragem;
    }

    public LocalDate getPeriodoComparacaoInicio() {
        return periodoComparacaoInicio;
    }

    public void setPeriodoComparacaoInicio(LocalDate periodoComparacaoInicio) {
        this.periodoComparacaoInicio = periodoComparacaoInicio;
    }

    public LocalDate getPeriodoComparacaoFim() {
        return periodoComparacaoFim;
    }

    public void setPeriodoComparacaoFim(LocalDate periodoComparacaoFim) {
        this.periodoComparacaoFim = periodoComparacaoFim;
    }
}