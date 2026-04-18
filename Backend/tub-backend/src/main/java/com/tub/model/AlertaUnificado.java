package com.tub.model;

/**
 * Linha 83: Entidade que representa um alerta após passar pela triagem.
 * É independente da plataforma de origem.
 */
public class AlertaUnificado {
    private Long id;
    private String titulo;
    private String descricao;
    private String severidade; // NORMALIZADO: CRITICO, MODERADO, LIGEIRO
    private String origem;     // BILHETICA, WAVECOM, DMS
    private String estado;     // PENDENTE, EM_RESOLUCAO, RESOLVIDO

    public AlertaUnificado(Long id, String titulo, String descricao, String severidade, String origem, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.severidade = severidade;
        this.origem = origem;
        this.estado = estado;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getSeveridade() { return severidade; }
    public String getOrigem() { return origem; }
    public String getEstado() { return estado; }
}
