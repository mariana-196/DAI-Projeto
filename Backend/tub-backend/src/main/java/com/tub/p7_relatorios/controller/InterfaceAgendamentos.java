package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.model.TarefaAgendada;
import com.tub.p7_relatorios.repository.TarefaAgendadaRepository;
import com.tub.p7_relatorios.service.ControloTarefasAutomaticas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tub.p1_autenticacao.annotation.RequerCargo;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/agendamentos")
@CrossOrigin(origins = "*")
@RequerCargo("ADMINISTRADOR")
public class InterfaceAgendamentos {

    @Autowired
    private ControloTarefasAutomaticas controloTarefas;

    @Autowired
    private TarefaAgendadaRepository tarefaRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ControloConsultaAuditoria auditService;

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

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

        try {
            auditService.registar(
                    getExecutorEmail(),
                    "ALTERAR_CONFIGURACAO",
                    "Relatórios",
                    getExecutorIp(),
                    "INFO",
                    "Agendamento de relatório configurado: " + salvo.getNomeTarefa() + " (Tipo: " + salvo.getTipoRelatorio() + ")"
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }

        return ResponseEntity.ok(salvo);
    }

    // Este endpoint permite a um administrador forçar a geração do relatório agora mesmo, 
    // sem ter de ficar à espera que sejam 2 da manhã!
    @PostMapping("/executar-agora")
    public ResponseEntity<String> dispararTarefaManualmente() {
        controloTarefas.executarAgendamentoAutomatico();

        try {
            auditService.registar(
                    getExecutorEmail(),
                    "EXPORTAR_RELATORIO",
                    "Relatórios",
                    getExecutorIp(),
                    "INFO",
                    "Tarefa automática de geração de relatórios disparada manualmente."
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }

        return ResponseEntity.ok("Tarefa automática de relatórios disparada manualmente com sucesso!");
    }

    // Cancelar/Eliminar um agendamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAgendamento(@PathVariable Long id) {
        TarefaAgendada agendamento = tarefaRepository.findById(id).orElse(null);
        if (agendamento != null) {
            tarefaRepository.deleteById(id);
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ALTERAR_CONFIGURACAO",
                        "Relatórios",
                        getExecutorIp(),
                        "INFO",
                        "Agendamento de relatório cancelado: " + agendamento.getNomeTarefa() + " (Tipo: " + agendamento.getTipoRelatorio() + ")"
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}