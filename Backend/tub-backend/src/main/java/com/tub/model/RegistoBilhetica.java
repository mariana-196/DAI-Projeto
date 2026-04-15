package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registos_bilhetica")
public class RegistoBilhetica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteBilhetica lote;

    @ManyToOne
    @JoinColumn(name = "viatura_id", nullable = true)
    private Viatura viatura;

    @ManyToOne
    @JoinColumn(name = "linha_id", nullable = false)
    private Linha linha;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String paragemOrigem;

    @Column(nullable = false)
    private String tipoTitulo;

    @Column(nullable = false)
    private Integer validacoes;

    private String zona;

    public RegistoBilhetica() {}

    public Long getId() { return id; }

    public LoteBilhetica getLote() { return lote; }
    public void setLote(LoteBilhetica lote) { this.lote = lote; }

    public Viatura getViatura() { return viatura; }
    public void setViatura(Viatura viatura) { this.viatura = viatura; }

    public Linha getLinha() { return linha; }
    public void setLinha(Linha linha) { this.linha = linha; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getParagemOrigem() { return paragemOrigem; }
    public void setParagemOrigem(String paragemOrigem) { this.paragemOrigem = paragemOrigem; }

    public String getTipoTitulo() { return tipoTitulo; }
    public void setTipoTitulo(String tipoTitulo) { this.tipoTitulo = tipoTitulo; }

    public Integer getValidacoes() { return validacoes; }
    public void setValidacoes(Integer validacoes) { this.validacoes = validacoes; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
}
