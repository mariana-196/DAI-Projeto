package com.tub.p8_gestao_bilhetica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;

@Entity
@Table(name = "registos_bilhetica")
public class RegistoBilhetica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteDadosBilhetica lote;

    @ManyToOne
    @JoinColumn(name = "viatura_id", nullable = true)
    private Viatura viatura;

    @ManyToOne
    @JoinColumn(name = "linha_id", nullable = false)
    private Linha linha;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String paragemOrigem;

    private String paragemDestino;

    @Column(nullable = false)
    private String tipoTitulo;

    @Column(nullable = false)
    private Integer validacoes;

    private String zona;

    private Double latitude;

    private Double longitude;

    private Double latitudeDestino;

    private Double longitudeDestino;

    public RegistoBilhetica() {}

    public Long getId() { return id; }

    public LoteDadosBilhetica getLote() { return lote; }
    public void setLote(LoteDadosBilhetica lote) { this.lote = lote; }

    public Viatura getViatura() { return viatura; }
    public void setViatura(Viatura viatura) { this.viatura = viatura; }

    public Linha getLinha() { return linha; }
    public void setLinha(Linha linha) { this.linha = linha; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getParagemOrigem() { return paragemOrigem; }
    public void setParagemOrigem(String paragemOrigem) { this.paragemOrigem = paragemOrigem; }

    public String getParagemDestino() { return paragemDestino; }
    public void setParagemDestino(String paragemDestino) { this.paragemDestino = paragemDestino; }

    public String getTipoTitulo() { return tipoTitulo; }
    public void setTipoTitulo(String tipoTitulo) { this.tipoTitulo = tipoTitulo; }

    public Integer getValidacoes() { return validacoes; }
    public void setValidacoes(Integer validacoes) { this.validacoes = validacoes; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitudeDestino() { return latitudeDestino; }
    public void setLatitudeDestino(Double latitudeDestino) { this.latitudeDestino = latitudeDestino; }

    public Double getLongitudeDestino() { return longitudeDestino; }
    public void setLongitudeDestino(Double longitudeDestino) { this.longitudeDestino = longitudeDestino; }
}
