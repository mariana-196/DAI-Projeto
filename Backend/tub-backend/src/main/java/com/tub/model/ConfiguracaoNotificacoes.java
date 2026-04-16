package com.tub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracoes_notificacoes")
public class ConfiguracaoNotificacoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean notificacoesAtivas;

    @Column(nullable = false)
    private String canalDefault;

    @Column(nullable = false)
    private String severidadeMinimaGlobal;

    private String emailDestinoDefault;

    public ConfiguracaoNotificacoes() {}

    public ConfiguracaoNotificacoes(Boolean notificacoesAtivas, String canalDefault, String severidadeMinimaGlobal, String emailDestinoDefault) {
        this.notificacoesAtivas = notificacoesAtivas;
        this.canalDefault = canalDefault;
        this.severidadeMinimaGlobal = severidadeMinimaGlobal;
        this.emailDestinoDefault = emailDestinoDefault;
    }

    public Long getId() { return id; }

    public Boolean getNotificacoesAtivas() { return notificacoesAtivas; }
    public void setNotificacoesAtivas(Boolean notificacoesAtivas) { this.notificacoesAtivas = notificacoesAtivas; }

    public String getCanalDefault() { return canalDefault; }
    public void setCanalDefault(String canalDefault) { this.canalDefault = canalDefault; }

    public String getSeveridadeMinimaGlobal() { return severidadeMinimaGlobal; }
    public void setSeveridadeMinimaGlobal(String severidadeMinimaGlobal) { this.severidadeMinimaGlobal = severidadeMinimaGlobal; }

    public String getEmailDestinoDefault() { return emailDestinoDefault; }
    public void setEmailDestinoDefault(String emailDestinoDefault) { this.emailDestinoDefault = emailDestinoDefault; }
}