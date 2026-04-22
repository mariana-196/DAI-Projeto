package com.tub.p10_gestao_pmd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_geograficos")
public class EventoGeografico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mantemos o Long viaturaId como tu querias!
    @Column(name = "viatura_id", nullable = false)
    private Long viaturaId;

    @Column(nullable = false)
    private String tipo; // "SAIDA_ZONA", "DESVIO_ROTA", "ENTRADA_ZONA"

    @Column(nullable = false)
    private String detalhes;

    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // Construtor vazio (obrigatório para o JPA/Hibernate)
    public EventoGeografico() {
    }

    // Este é o Construtor que o teu MapaOperacaoService está a usar!
    public EventoGeografico(Long id, Long viaturaId, String tipo, String detalhes) {
        this.id = id;
        this.viaturaId = viaturaId;
        this.tipo = tipo;
        this.detalhes = detalhes;
        this.timestamp = LocalDateTime.now(); // Regista a hora exata
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getViaturaId() { return viaturaId; }
    public void setViaturaId(Long viaturaId) { this.viaturaId = viaturaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    
    public void setTipoEvento(String tipoEvento) { this.tipo = tipoEvento; }

    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
    
    
    public void setZona(String zona) { this.detalhes = zona; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}