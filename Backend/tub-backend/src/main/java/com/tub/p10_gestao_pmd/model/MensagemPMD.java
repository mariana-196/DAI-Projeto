package com.tub.p10_gestao_pmd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens_pmd")
public class MensagemPMD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String conteudo;

    @Column(nullable = false)
    private String prioridade; // BAIXA, MEDIA, ALTA

    @Column(nullable = false)
    private String estado; // ATIVA, AGENDADA, INATIVA

    @Column(nullable = false)
    private String panelId; // Cód. do Painel (ex: PMD-001) ou TODOS

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public MensagemPMD() {}

    public Long getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPanelId() { return panelId; }
    public void setPanelId(String panelId) { this.panelId = panelId; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}