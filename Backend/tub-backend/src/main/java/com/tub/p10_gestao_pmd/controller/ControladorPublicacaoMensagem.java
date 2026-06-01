package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.model.CatalogoMensagensRapidas;
import com.tub.p10_gestao_pmd.model.MensagemPMD;
import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibicao;
import com.tub.p10_gestao_pmd.repository.CatalogoMensagensRapidasRepository;
import com.tub.p10_gestao_pmd.repository.MensagemPMDRepository;
import com.tub.p10_gestao_pmd.repository.RepositorioTarefasExibicaoRepository;
import com.tub.p10_gestao_pmd.service.PainelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/paineis")
@CrossOrigin(origins = "*")
public class ControladorPublicacaoMensagem {

    @Autowired
    private PainelService painelService;

    @Autowired
    private MensagemPMDRepository mensagemRepository;

    @Autowired
    private RepositorioTarefasExibicaoRepository tarefasRepository;

    @Autowired
    private CatalogoMensagensRapidasRepository modelosRepository;

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

    /**
     * Publica uma mensagem em tempo real.
     */
    @PostMapping("/publicar")
    public ResponseEntity<?> publicarMensagem(@RequestBody Map<String, String> payload) {
        try {
            String panelId = payload.get("panelId");
            String message = payload.get("message");

            if (panelId == null || message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"erro\": \"Dados incompletos.\"}");
            }

            if ("TODOS".equals(panelId)) {
                painelService.publicarMensagemBroadcast(message);
            } else {
                painelService.publicarMensagemNumPainel(panelId, message);
            }

            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "INFO",
                        "Mensagem publicada em tempo real no painel " + panelId + ": \"" + message + "\""
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }

            return ResponseEntity.ok("{\"resultado\": \"Mensagem publicada e guardada na BD com sucesso!\"}");

        } catch (IllegalStateException | IllegalArgumentException e) {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "ERRO",
                        "Falha ao publicar mensagem em tempo real: " + e.getMessage()
                );
            } catch (Exception ex) {
                System.err.println("Erro ao registar auditoria: " + ex.getMessage());
            }
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "ERRO",
                        "Erro interno ao publicar mensagem em tempo real"
                );
            } catch (Exception ex) {
                System.err.println("Erro ao registar auditoria: " + ex.getMessage());
            }
            return ResponseEntity.internalServerError().body("{\"erro\": \"Erro ao processar o pedido no servidor.\"}");
        }
    }

    /**
     * Agenda uma mensagem com verificação de conflitos temporais (+/- 10 minutos).
     */
    @PostMapping("/agendar")
    public ResponseEntity<?> agendarMensagem(@RequestBody Map<String, String> payload) {
        try {
            String panelId = payload.get("panelId");
            String conteudo = payload.get("mensagem");
            String dataStr = payload.get("data");
            String horaStr = payload.get("hora");

            if (panelId == null || conteudo == null || dataStr == null || horaStr == null) {
                return ResponseEntity.badRequest().body("{\"erro\": \"Parâmetros obrigatórios ausentes.\"}");
            }

            LocalDateTime dataHora = LocalDateTime.parse(dataStr + "T" + horaStr + ":00");

            // Validar conflitos temporais (+/- 10 minutos)
            List<RepositorioTarefasExibicao> tarefasPendentes = tarefasRepository.findAll().stream()
                    .filter(t -> t.getConcluida() != null && !t.getConcluida())
                    .toList();

            for (RepositorioTarefasExibicao tarefa : tarefasPendentes) {
                MensagemPMD msg = mensagemRepository.findById(tarefa.getMensagemId()).orElse(null);
                if (msg != null) {
                    boolean mesmoPainelOuBroadcast = msg.getPanelId().equals(panelId) || 
                                                   "TODOS".equals(msg.getPanelId()) || 
                                                   "TODOS".equals(panelId);
                    if (mesmoPainelOuBroadcast) {
                        long diff = Duration.between(tarefa.getDataHoraExibicao(), dataHora).abs().toMinutes();
                        if (diff < 10) {
                            return ResponseEntity.badRequest().body("{\"erro\": \"Conflito temporal: já existe uma mensagem agendada para este painel ou broadcast no intervalo de 10 minutos (em " + tarefa.getDataHoraExibicao().toString().replace("T", " ") + ").\"}");
                        }
                    }
                }
            }

            // Criar e salvar MensagemPMD em estado "AGENDADA"
            MensagemPMD msg = new MensagemPMD();
            msg.setTitulo("Mensagem Programada");
            msg.setConteudo(conteudo);
            msg.setPrioridade("MEDIA");
            msg.setEstado("AGENDADA");
            msg.setPanelId(panelId);
            msg.setDataCriacao(LocalDateTime.now());
            msg = mensagemRepository.save(msg);

            // Criar tarefa de exibição
            RepositorioTarefasExibicao tarefa = new RepositorioTarefasExibicao();
            tarefa.setMensagemId(msg.getId());
            tarefa.setDataHoraExibicao(dataHora);
            tarefa.setConcluida(false);
            tarefasRepository.save(tarefa);

            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "INFO",
                        "Mensagem agendada para o painel " + panelId + " na data/hora " + dataHora + ": \"" + conteudo + "\""
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }

            return ResponseEntity.ok("{\"status\": \"Sucesso\", \"mensagem\": \"Mensagem agendada com sucesso!\"}");

        } catch (Exception e) {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "ERRO",
                        "Falha ao agendar mensagem para painel: " + e.getMessage()
                );
            } catch (Exception ex) {
                System.err.println("Erro ao registar auditoria: " + ex.getMessage());
            }
            return ResponseEntity.badRequest().body("{\"erro\": \"Erro ao processar agendamento: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Retorna a lista de modelos de mensagens rápidas (catálogo).
     */
    @GetMapping("/modelos")
    public ResponseEntity<List<CatalogoMensagensRapidas>> obterModelos() {
        List<CatalogoMensagensRapidas> modelos = modelosRepository.findAll();
        return ResponseEntity.ok(modelos);
    }

    /**
     * Retorna a lista de agendamentos pendentes com informações detalhadas da mensagem.
     */
    @GetMapping("/agendados")
    public ResponseEntity<?> obterAgendados() {
        List<RepositorioTarefasExibicao> tarefas = tarefasRepository.findAll().stream()
                .filter(t -> t.getConcluida() != null && !t.getConcluida())
                .sorted(Comparator.comparing(RepositorioTarefasExibicao::getDataHoraExibicao))
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (RepositorioTarefasExibicao tarefa : tarefas) {
            MensagemPMD msg = mensagemRepository.findById(tarefa.getMensagemId()).orElse(null);
            if (msg != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", tarefa.getId());
                map.put("mensagemId", msg.getId());
                map.put("dataHoraExibicao", tarefa.getDataHoraExibicao());
                map.put("panelId", msg.getPanelId());
                map.put("conteudo", msg.getConteudo());
                map.put("prioridade", msg.getPrioridade());
                result.add(map);
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Cancela um agendamento pendente.
     */
    @DeleteMapping("/agendados/{id}")
    public ResponseEntity<?> cancelarAgendamento(@PathVariable Long id) {
        Optional<RepositorioTarefasExibicao> optTarefa = tarefasRepository.findById(id);
        if (optTarefa.isPresent()) {
            RepositorioTarefasExibicao tarefa = optTarefa.get();
            // Remover ou atualizar o estado da mensagem para INATIVA
            Optional<MensagemPMD> optMsg = mensagemRepository.findById(tarefa.getMensagemId());
            if (optMsg.isPresent()) {
                MensagemPMD msg = optMsg.get();
                msg.setEstado("INATIVA");
                mensagemRepository.save(msg);
            }
            tarefasRepository.delete(tarefa);

            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "INFO",
                        "Agendamento de mensagem cancelado (Tarefa ID: " + id + ")"
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }

            return ResponseEntity.ok("{\"mensagem\": \"Agendamento cancelado com sucesso!\"}");
        } else {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_PAINEL",
                        "Painéis PMD/DMS",
                        getExecutorIp(),
                        "AVISO",
                        "Tentativa de cancelar agendamento inexistente (Tarefa ID: " + id + ")"
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            return ResponseEntity.badRequest().body("{\"erro\": \"Agendamento não encontrado.\"}");
        }
    }
}
