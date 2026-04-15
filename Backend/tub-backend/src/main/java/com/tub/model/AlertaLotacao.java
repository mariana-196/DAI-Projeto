package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_lotacao")
public class AlertaLotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viatura_id", nullable = false)
    private Viatura viatura;

    @Column(nullable = false)
    private String linha;

    @Column(nullable = false)
    private String severidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AlertaLotacao() {}

    public AlertaLotacao(Viatura viatura, String linha, String severidade, String estado, String descricao) {
        this.viatura = viatura;
        this.linha = linha;
        this.severidade = severidade;
        this.estado = estado;
        this.descricao = descricao;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Viatura getViatura() { return viatura; }
    public void setViatura(Viatura viatura) { this.viatura = viatura; }

    public String getLinha() { return linha; }
    public void setLinha(String linha) { this.linha = linha; }

    public String getSeveridade() { return severidade; }
    public void setSeveridade(String severidade) { this.severidade = severidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getTimestamp() { return timestamp; }
}
