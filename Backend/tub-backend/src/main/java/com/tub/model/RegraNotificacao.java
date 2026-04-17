package com.tub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "regras_notificacao")
public class RegraNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoEvento;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private Boolean ativa;

    @Column(nullable = false)
    private String canal;

    @Column(nullable = false)
    private String severidadeMinima;

    public RegraNotificacao() {}

    public RegraNotificacao(String tipoEvento, String destinatario, Boolean ativa, String canal, String severidadeMinima) {
        this.tipoEvento = tipoEvento;
        this.destinatario = destinatario;
        this.ativa = ativa;
        this.canal = canal;
        this.severidadeMinima = severidadeMinima;
    }

    public Long getId() { return id; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public Boolean getAtiva() { return ativa; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getSeveridadeMinima() { return severidadeMinima; }
    public void setSeveridadeMinima(String severidadeMinima) { this.severidadeMinima = severidadeMinima; }
}