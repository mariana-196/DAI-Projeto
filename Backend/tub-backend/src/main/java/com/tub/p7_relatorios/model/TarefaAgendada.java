package com.tub.p7_relatorios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas_agendadas")
public class TarefaAgendada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeTarefa;
    private LocalDateTime dataHoraExecucao;
    private String estado; // Ex: "SUCESSO", "FALHA", "EM_EXECUCAO"

    // Construtor vazio que o Hibernate exige
    public TarefaAgendada() {}

    // Construtor auxiliar para facilitar a criação no serviço
    public TarefaAgendada(String nomeTarefa, String estado) {
        this.nomeTarefa = nomeTarefa;
        this.estado = estado;
        this.dataHoraExecucao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeTarefa() { return nomeTarefa; }
    public void setNomeTarefa(String nomeTarefa) { this.nomeTarefa = nomeTarefa; }
    public LocalDateTime getDataHoraExecucao() { return dataHoraExecucao; }
    public void setDataHoraExecucao(LocalDateTime dataHoraExecucao) { this.dataHoraExecucao = dataHoraExecucao; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}