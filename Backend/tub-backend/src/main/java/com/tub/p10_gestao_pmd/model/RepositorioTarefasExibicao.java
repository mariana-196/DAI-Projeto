package com.tub.p10_gestao_pmd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas_exibicao")
public class RepositorioTarefasExibicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mensagemId;

    @Column(nullable = false)
    private LocalDateTime dataHoraExibicao;

    @Column(nullable = false)
    private Boolean concluida = false;

    // --- Getters e Setters obrigatórios ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMensagemId() { return mensagemId; }
    public void setMensagemId(Long mensagemId) { this.mensagemId = mensagemId; }

    public LocalDateTime getDataHoraExibicao() { return dataHoraExibicao; }
    public void setDataHoraExibicao(LocalDateTime dataHoraExibicao) { this.dataHoraExibicao = dataHoraExibicao; }

    public Boolean getConcluida() { return concluida; }
    public void setConcluida(Boolean concluida) { this.concluida = concluida; }
}