package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.model.TarefaAgendada;
import com.tub.p7_relatorios.repository.TarefaAgendadaRepository;
import com.tub.p7_relatorios.service.ControloTarefasAutomaticas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/agendamentos")
@CrossOrigin(origins = "*")
public class InterfaceAgendamentos {

    @Autowired
    private ControloTarefasAutomaticas controloTarefas;

    @Autowired
    private TarefaAgendadaRepository tarefaRepository;

    // Listar todos os agendamentos configurados
    @GetMapping
    public ResponseEntity<List<TarefaAgendada>> listarAgendamentos() {
        return ResponseEntity.ok(tarefaRepository.findAll());
    }

    // Criar um novo agendamento de relatório (U 7.4.1.i / U 7.4.1.d)
    @PostMapping
    public ResponseEntity<TarefaAgendada> criarAgendamento(@RequestBody TarefaAgendada novoAgendamento) {
        if (novoAgendamento.getNomeTarefa() == null || novoAgendamento.getNomeTarefa().isEmpty()) {
            novoAgendamento.setNomeTarefa("EnvioAutomatico_" + novoAgendamento.getTipoRelatorio() + "_" + System.currentTimeMillis());
        }
        novoAgendamento.setDataHoraExecucao(java.time.LocalDateTime.now());
        novoAgendamento.setEstado("CONFIGURADO");
        TarefaAgendada salvo = tarefaRepository.save(novoAgendamento);
        return ResponseEntity.ok(salvo);
    }

    // Este endpoint permite a um administrador forçar a geração do relatório agora mesmo, 
    // sem ter de ficar à espera que sejam 2 da manhã!
    @PostMapping("/executar-agora")
    public ResponseEntity<String> dispararTarefaManualmente() {
        controloTarefas.executarAgendamentoAutomatico();
        return ResponseEntity.ok("Tarefa automática de relatórios disparada manualmente com sucesso!");
    }
}