package com.tub.model;

import java.util.List;

public class ContextoAlerta {
    private Long id;
    private String titulo;
    private String descricao;
    private String origem; // Ex: Plataforma Wavecom, Bilhética
    private String timestamp;
    private String prioridade;
    private String estado;
    private String infoAdicional; // Metadados técnicos
    private List<String> historico; // LINHA 88: Histórico de alterações

    public ContextoAlerta(Long id, String titulo, String descricao, String origem, 
                          String prioridade, String estado, String infoAdicional, List<String> historico) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.origem = origem;
        this.timestamp = java.time.LocalDateTime.now().toString();
        this.prioridade = prioridade;
        this.estado = estado;
        this.infoAdicional = infoAdicional;
        this.historico = historico;
    }

    // Getters (Obrigatórios para o Controller devolver JSON)
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getOrigem() { return origem; }
    public String getTimestamp() { return timestamp; }
    public String getPrioridade() { return prioridade; }
    public String getEstado() { return estado; }
    public String getInfoAdicional() { return infoAdicional; }
    public List<String> getHistorico() { return historico; }
}
