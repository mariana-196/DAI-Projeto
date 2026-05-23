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

    // Parametrizador do Agendamento (4SRS U 7.4.1.d)
    private String emailDestinatario;
    private String periodicidade; // Ex: "DIARIA", "SEMANAL", "MENSAL"
    private String formato;       // Ex: "PDF", "CSV"
    private String tipoRelatorio; // Ex: "OPERACIONAL", "AUDITORIA"
    private boolean ativo = true;

    // Construtor vazio que o Hibernate exige
    public TarefaAgendada() {}

    // Construtor auxiliar para facilitar a criação no serviço
    public TarefaAgendada(String nomeTarefa, String estado) {
        this.nomeTarefa = nomeTarefa;
        this.estado = estado;
        this.dataHoraExecucao = LocalDateTime.now();
    }

    public TarefaAgendada(String nomeTarefa, String emailDestinatario, String periodicidade, String formato, String tipoRelatorio) {
        this.nomeTarefa = nomeTarefa;
        this.emailDestinatario = emailDestinatario;
        this.periodicidade = periodicidade;
        this.formato = formato;
        this.tipoRelatorio = tipoRelatorio;
        this.estado = "CONFIGURADO";
        this.dataHoraExecucao = LocalDateTime.now();
        this.ativo = true;
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

    public String getEmailDestinatario() { return emailDestinatario; }
    public void setEmailDestinatario(String emailDestinatario) { this.emailDestinatario = emailDestinatario; }
    public String getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(String periodicidade) { this.periodicidade = periodicidade; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
    public String getTipoRelatorio() { return tipoRelatorio; }
    public void setTipoRelatorio(String tipoRelatorio) { this.tipoRelatorio = tipoRelatorio; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}