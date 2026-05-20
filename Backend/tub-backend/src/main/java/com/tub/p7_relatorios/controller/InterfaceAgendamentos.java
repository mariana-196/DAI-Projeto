package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.service.ControloTarefasAutomaticas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios/agendamentos")
public class InterfaceAgendamentos {

    @Autowired
    private ControloTarefasAutomaticas controloTarefas;

    // Este endpoint permite a um administrador forçar a geração do relatório agora mesmo, 
    // sem ter de ficar à espera que sejam 2 da manhã!
    @PostMapping("/executar-agora")
    public ResponseEntity<String> dispararTarefaManualmente() {
        controloTarefas.executarAgendamentoAutomatico();
        return ResponseEntity.ok("Tarefa automática de relatórios disparada manualmente com sucesso!");
    }
}