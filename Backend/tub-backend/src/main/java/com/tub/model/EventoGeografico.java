package com.tub.model;

public class EventoGeografico {
    private Long id;
    private Long viaturaId;
    private String tipo; // "SAIDA_ZONA", "DESVIO_ROTA", "ENTRADA_ZONA"
    private String timestamp;
    private String detalhes;

    // Construtor vazio (obrigatório para algumas bibliotecas do Java)
    public EventoGeografico() {
    }

    // Este é o Construtor que o teu MapaOperacaoService está a tentar usar!
    public EventoGeografico(Long id, Long viaturaId, String tipo, String detalhes) {
        this.id = id;
        this.viaturaId = viaturaId;
        this.tipo = tipo;
        this.detalhes = detalhes;
        this.timestamp = java.time.LocalDateTime.now().toString(); // Regista a hora exata automaticamente
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getViaturaId() { return viaturaId; }
    public void setViaturaId(Long viaturaId) { this.viaturaId = viaturaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
}