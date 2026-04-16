package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lotacao_viatura")
public class LotacaoViatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "viatura_id", nullable = false, unique = true)
    private Viatura viatura;

    @Column(nullable = false)
    private String linha;

    @Column(nullable = false)
    private Integer passageirosAtuais;

    @Column(nullable = false)
    private Double taxaOcupacao;

    @Column(nullable = false)
    private boolean sinalAtivo = true;

    @Column(nullable = false)
    private LocalDateTime ultimaAtualizacao = LocalDateTime.now();

    public LotacaoViatura() {}

    public Long getId() { return id; }

    public Viatura getViatura() { return viatura; }
    public void setViatura(Viatura viatura) { this.viatura = viatura; }

    public String getLinha() { return linha; }
    public void setLinha(String linha) { this.linha = linha; }

    public Integer getPassageirosAtuais() { return passageirosAtuais; }
    public void setPassageirosAtuais(Integer passageirosAtuais) { this.passageirosAtuais = passageirosAtuais; }

    public Double getTaxaOcupacao() { return taxaOcupacao; }
    public void setTaxaOcupacao(Double taxaOcupacao) { this.taxaOcupacao = taxaOcupacao; }

    public boolean isSinalAtivo() { return sinalAtivo; }
    public void setSinalAtivo(boolean sinalAtivo) { this.sinalAtivo = sinalAtivo; }

    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}