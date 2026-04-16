package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_lotacao")
public class HistoricoLotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viatura_id", nullable = false)
    private Viatura viatura;

    @Column(nullable = false)
    private Integer variacao;

    @Column(nullable = false)
    private Integer passageirosResultantes;

    @Column(nullable = false)
    private String tipoEvento;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public HistoricoLotacao() {}

    public Long getId() { return id; }

    public Viatura getViatura() { return viatura; }
    public void setViatura(Viatura viatura) { this.viatura = viatura; }

    public Integer getVariacao() { return variacao; }
    public void setVariacao(Integer variacao) { this.variacao = variacao; }

    public Integer getPassageirosResultantes() { return passageirosResultantes; }
    public void setPassageirosResultantes(Integer passageirosResultantes) { this.passageirosResultantes = passageirosResultantes; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public LocalDateTime getTimestamp() { return timestamp; }
}