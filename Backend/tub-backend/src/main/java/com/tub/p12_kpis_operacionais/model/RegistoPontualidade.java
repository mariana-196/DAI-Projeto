package com.tub.p12_kpis_operacionais.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registos_pontualidade")
public class RegistoPontualidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String linha;

    @Column(nullable = false)
    private String viatura;

    // 100% (pontual) a 0% (muito atrasado)
    @Column(nullable = false)
    private Integer percentagemPontualidade;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public RegistoPontualidade() {}

    public RegistoPontualidade(String linha, String viatura, Integer percentagemPontualidade) {
        this.linha = linha;
        this.viatura = viatura;
        this.percentagemPontualidade = percentagemPontualidade;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getLinha() { return linha; }
    public void setLinha(String linha) { this.linha = linha; }
    public String getViatura() { return viatura; }
    public void setViatura(String viatura) { this.viatura = viatura; }
    public Integer getPercentagemPontualidade() { return percentagemPontualidade; }
    public void setPercentagemPontualidade(Integer percentagemPontualidade) { this.percentagemPontualidade = percentagemPontualidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
