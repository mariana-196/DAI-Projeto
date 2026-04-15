package com.tub.controller;

import com.tub.model.RegistoAuditoria;
import com.tub.service.AuditService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuditoriaController {

    private final AuditService auditService;

    public AuditoriaController(AuditService auditService) {
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