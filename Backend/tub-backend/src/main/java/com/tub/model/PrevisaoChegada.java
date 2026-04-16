package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "previsoes_chegada")
public class PrevisaoChegada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viatura_id", nullable = false)
    private Viatura viatura;

    @ManyToOne
    @JoinColumn(name = "painel_id", nullable = false)
    private PainelPMD painel;

    @ManyToOne
    @JoinColumn(name = "linha_id", nullable = false)
    private Linha linha;

    @Column(nullable = false)
    private String destino;

    @Column(nullable = false)
    private Integer etaMinutos;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public PrevisaoChegada() {}

    public Long getId() { return id; }

    public Viatura getViatura() { return viatura; }
    public void setViatura(Viatura viatura) { this.viatura = viatura; }

    public PainelPMD getPainel() { return painel; }
    public void setPainel(PainelPMD painel) { this.painel = painel; }

    public Linha getLinha() { return linha; }
    public void setLinha(Linha linha) { this.linha = linha; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public Integer getEtaMinutos() { return etaMinutos; }
    public void setEtaMinutos(Integer etaMinutos) { this.etaMinutos = etaMinutos; }

    public LocalDateTime getTimestamp() { return timestamp; }
}