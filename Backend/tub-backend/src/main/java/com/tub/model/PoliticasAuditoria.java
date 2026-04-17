package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "politicas_auditoria")
public class PoliticasAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nivelMinimo;

    @Column(nullable = false)
    private Integer diasRetencao;

    @Column(nullable = false)
    private Boolean notificacoesAtivas;

    private String emailNotificacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    public PoliticasAuditoria() {}

    public PoliticasAuditoria(String nivelMinimo, Integer diasRetencao, Boolean notificacoesAtivas, String emailNotificacao) {
        this.nivelMinimo = nivelMinimo;
        this.diasRetencao = diasRetencao;
        this.notificacoesAtivas = notificacoesAtivas;
        this.emailNotificacao = emailNotificacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getNivelMinimo() { return nivelMinimo; }
    public void setNivelMinimo(String nivelMinimo) { this.nivelMinimo = nivelMinimo; }

    public Integer getDiasRetencao() { return diasRetencao; }
    public void setDiasRetencao(Integer diasRetencao) { this.diasRetencao = diasRetencao; }

    public Boolean getNotificacoesAtivas() { return notificacoesAtivas; }
    public void setNotificacoesAtivas(Boolean notificacoesAtivas) { this.notificacoesAtivas = notificacoesAtivas; }

    public String getEmailNotificacao() { return emailNotificacao; }
    public void setEmailNotificacao(String emailNotificacao) { this.emailNotificacao = emailNotificacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}