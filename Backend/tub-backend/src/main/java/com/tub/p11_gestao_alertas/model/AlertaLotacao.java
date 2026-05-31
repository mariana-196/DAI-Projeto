package com.tub.p11_gestao_alertas.model;

/**
 * Classe definida na arquitetura original do sistema para representar um alerta
 * específico de lotação excessiva ou crítica.
 * Atualmente as suas funções lógicas foram unificadas na entidade 'AlertaOperacional', 
 * mas a classe é mantida por requisitos de coerência com os diagramas de arquitetura.
 */
public class AlertaLotacao {

    private Long id;
    private String viaturaId;
    private Integer capacidadeMaxima;
    private Integer passageirosAtuais;
    private Double taxaOcupacao;

    public AlertaLotacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getViaturaId() { return viaturaId; }
    public void setViaturaId(String viaturaId) { this.viaturaId = viaturaId; }

    public Integer getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(Integer capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public Integer getPassageirosAtuais() { return passageirosAtuais; }
    public void setPassageirosAtuais(Integer passageirosAtuais) { this.passageirosAtuais = passageirosAtuais; }

    public Double getTaxaOcupacao() { return taxaOcupacao; }
    public void setTaxaOcupacao(Double taxaOcupacao) { this.taxaOcupacao = taxaOcupacao; }
}
