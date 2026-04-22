package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.RegistoAuditoria;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("controladorConsultaAuditoria")
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ControloConsultaAuditoria {

    private final com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService;

    public ControloConsultaAuditoria(
            com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/logs")
    public List<RegistoAuditoria> getLogs(
            @RequestParam(required = false) String utilizador,
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim
    ) {
        return auditService.pesquisarLogs(utilizador, acao, modulo, nivel, dataInicio, dataFim);
    }

    @PostMapping("/logs")
    public RegistoAuditoria criarLog(@RequestBody RegistoAuditoria registo) {
        return auditService.guardarLog(registo);
    }
}