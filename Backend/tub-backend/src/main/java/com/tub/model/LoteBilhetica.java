package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lotes_bilhetica")
public class LoteBilhetica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoLote;

    @Column(nullable = false)
    private LocalDateTime dataImportacao = LocalDateTime.now();

    @Column(nullable = false)
    private String origem;

    @Column(nullable = false)
    private String estado; // RECEBIDO, VALIDADO, PROCESSADO, ERRO

    public LoteBilhetica() {}

    public Long getId() { return id; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public LocalDateTime getDataImportacao() { return dataImportacao; }
    public void setDataImportacao(LocalDateTime dataImportacao) { this.dataImportacao = dataImportacao; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
