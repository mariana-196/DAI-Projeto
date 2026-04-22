package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas_exibicao") 
public class RepositorioTarefasExibicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guarda qual é a mensagem que vai ser mostrada (liga-se à tabela MensagemPMD)
    @Column(name = "mensagem_id", nullable = false)
    private Long mensagemId;

    // A data e hora exata em que o aviso deve saltar para os ecrãs
    @Column(name = "data_hora_exibicao", nullable = false)
    private LocalDateTime dataHoraExibicao;

    // Quanto tempo o aviso fica no ecrã (ex: 60 minutos)
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    // Diz se a tarefa já passou/foi concluída (true) ou se ainda está pendente (false)
    @Column(name = "concluida")
    private Boolean concluida;

    // ---------------------------------------------------
    // Construtor vazio (Obrigatório para o Spring Boot/JPA)
    // ---------------------------------------------------
    public RepositorioTarefasExibicao() {
        this.concluida = false; // Por defeito, quando criamos, não está concluída
    }

    // ---------------------------------------------------
    // Getters e Setters
    // ---------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMensagemId() {
        return mensagemId;
    }

    public void setMensagemId(Long mensagemId) {
        this.mensagemId = mensagemId;
    }

    public LocalDateTime getDataHoraExibicao() {
        return dataHoraExibicao;
    }

    public void setDataHoraExibicao(LocalDateTime dataHoraExibicao) {
        this.dataHoraExibicao = dataHoraExibicao;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Boolean getConcluida() {
        return concluida;
    }

    public void setConcluida(Boolean concluida) {
        this.concluida = concluida;
    }
}