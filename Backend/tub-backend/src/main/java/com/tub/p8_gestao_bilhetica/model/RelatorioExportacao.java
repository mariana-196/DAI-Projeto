package com.tub.p8_gestao_bilhetica.model;

import java.time.LocalDateTime;

/**
 * Classe definida na arquitetura original do sistema para representar
 * a exportação de um relatório de bilhética.
 * Atualmente o sistema utiliza uma exportação direta (stream) via CSV e PDF
 * em tempo real, mas a classe é mantida por requisitos arquiteturais dos diagramas.
 */
public class RelatorioExportacao {

    private Long id;
    private String nomeFicheiro;
    private String formato;
    private LocalDateTime dataGeracao;
    private String utilizadorGerador;

    public RelatorioExportacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeFicheiro() { return nomeFicheiro; }
    public void setNomeFicheiro(String nomeFicheiro) { this.nomeFicheiro = nomeFicheiro; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    public String getUtilizadorGerador() { return utilizadorGerador; }
    public void setUtilizadorGerador(String utilizadorGerador) { this.utilizadorGerador = utilizadorGerador; }
}
