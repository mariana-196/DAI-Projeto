package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.DadosRelatorio;
import com.tub.p7_relatorios.service.ControloAnonimizacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tub.p1_autenticacao.annotation.RequerCargo;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios/auditoria")
@CrossOrigin(origins = "*")
@RequerCargo("ADMINISTRADOR")
public class ControloRelatorioAuditoria {

    private final ControloAnonimizacao controloAnonimizacao;
    private final ControloConsultaAuditoria auditService;

    @Autowired
    private HttpServletRequest request;

    public ControloRelatorioAuditoria(ControloAnonimizacao controloAnonimizacao, ControloConsultaAuditoria auditService) {
        this.controloAnonimizacao = controloAnonimizacao;
        this.auditService = auditService;
    }

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    @PostMapping
    public ResponseEntity<DadosRelatorio> gerarRelatorioAuditoria(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String utilizador,
            @RequestParam(required = false) String severidade,
            @RequestParam(required = false) String evento
    ) {
        List<Map<String, Object>> dados = new ArrayList<>();

        LocalDateTime inicio = null;
        LocalDateTime fim = null;
        if (dataInicio != null && !dataInicio.isEmpty()) {
            inicio = java.time.LocalDate.parse(dataInicio).atStartOfDay();
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            fim = java.time.LocalDate.parse(dataFim).atTime(23, 59, 59);
        }

        List<com.tub.p6_auditoria.model.RegistoAuditoria> logs = auditService.pesquisarLogs(
                utilizador, evento, null, severidade, inicio, fim
        );

        for (com.tub.p6_auditoria.model.RegistoAuditoria log : logs) {
            Map<String, Object> registo = new LinkedHashMap<>();
            registo.put("timestamp", log.getTimestamp() != null ? log.getTimestamp().toString() : "N/A");
            registo.put("utilizador", log.getUtilizador());
            registo.put("acao", log.getAcao());
            registo.put("modulo", log.getModulo());
            registo.put("nivel", log.getNivel());
            registo.put("ipOrigem", log.getIpOrigem());
            dados.add(registo);
        }

        DadosRelatorio relatorio = new DadosRelatorio(
                "Relatório de Auditoria",
                "AUDITORIA",
                LocalDateTime.now(),
                "Administrador",
                dados
        );

        try {
            auditService.registar(
                    getExecutorEmail(),
                    "CONSULTAR_RELATORIO",
                    "Relatórios",
                    getExecutorIp(),
                    "INFO",
                    "Relatório de auditoria consultado. Filtros - Início: " + dataInicio + ", Fim: " + dataFim + ", Utilizador: " + utilizador + ", Severidade: " + severidade + ", Evento: " + evento
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }

        return ResponseEntity.ok(relatorio);
    }
}