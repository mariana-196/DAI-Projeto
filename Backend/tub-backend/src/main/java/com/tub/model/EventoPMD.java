package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_pmd")
public class EventoPMD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "painel_id", nullable = false)
    private PainelPMD painel;

    @Column(nullable = false)
    private String tipoEvento; // ERRO, OFFLINE, ONLINE, DEGRADADO, AVISO

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private String severidade; // BAIXA, MEDIA, ALTA, CRITICA

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public EventoPMD() {}

    public Long getId() { return id; }

    public PainelPMD getPainel() { return painel; }
    public void setPainel(PainelPMD painel) { this.painel = painel; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getSeveridade() { return severidade; }
    public void setSeveridade(String severidade) { this.severidade = severidade; }

    public LocalDateTime getTimestamp() { return timestamp; }
}
