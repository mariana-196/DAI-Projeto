package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metricas_ingestao")
public class MetricaIngestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lote_id", nullable = false, unique = true)
    private LoteBilhetica lote;

    @Column(nullable = false)
    private Integer registosRecebidos;

    @Column(nullable = false)
    private Integer registosValidos;

    @Column(nullable = false)
    private Integer registosInvalidos;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public MetricaIngestao() {}

    public Long getId() { return id; }

    public LoteBilhetica getLote() { return lote; }
    public void setLote(LoteBilhetica lote) { this.lote = lote; }

    public Integer getRegistosRecebidos() { return registosRecebidos; }
    public void setRegistosRecebidos(Integer registosRecebidos) { this.registosRecebidos = registosRecebidos; }

    public Integer getRegistosValidos() { return registosValidos; }
    public void setRegistosValidos(Integer registosValidos) { this.registosValidos = registosValidos; }

    public Integer getRegistosInvalidos() { return registosInvalidos; }
    public void setRegistosInvalidos(Integer registosInvalidos) { this.registosInvalidos = registosInvalidos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getTimestamp() { return timestamp; }
}
