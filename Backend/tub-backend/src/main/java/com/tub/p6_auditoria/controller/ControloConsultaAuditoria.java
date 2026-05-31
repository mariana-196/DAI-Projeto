package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.RegistoAuditoria;
import com.tub.p7_relatorios.service.ControloAnonimizacao;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import com.tub.p1_autenticacao.annotation.RequerCargo;

@RestController("controladorConsultaAuditoria")
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*")
@RequerCargo("ADMINISTRADOR")
public class ControloConsultaAuditoria {

    private final com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService;
    private final ControloAnonimizacao controloAnonimizacao;

    public ControloConsultaAuditoria(
            com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService,
            ControloAnonimizacao controloAnonimizacao) {
        this.auditService = auditService;
        this.controloAnonimizacao = controloAnonimizacao;
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

    @GetMapping("/exportar")
    public ResponseEntity<String> exportarRelatorioCsv(
            @RequestParam(required = false) String utilizador,
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        List<RegistoAuditoria> logs = auditService.pesquisarLogs(utilizador, acao, modulo, nivel, dataInicio, dataFim);
    
        StringBuilder csv = new StringBuilder();
        csv.append('\ufeff');
        csv.append("Data;Utilizador;Acao;Modulo;IP;Nivel;Detalhe\n"); // Cabeçalho (usamos ';' para o Excel em PT abrir bem)
    
        for (RegistoAuditoria log : logs) {
            csv.append(campoCsv(log.getTimestamp())).append(";")
                .append(campoCsv(controloAnonimizacao.anonimizarEmail(log.getUtilizador()))).append(";")
                .append(campoCsv(log.getAcao())).append(";")
                .append(campoCsv(log.getModulo())).append(";")
                .append(campoCsv(anonimizarIp(log.getIpOrigem()))).append(";")
                .append(campoCsv(log.getNivel())).append(";")
                .append(campoCsv(log.getDetalhe())).append("\n");
        }

    // 3. Devolve o ficheiro para o browser fazer download
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_auditoria_tub_anonimizado.csv")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csv.toString());
    }

    private String anonimizarIp(String ip) {
        return "***.***.***.***";
    }

    private String campoCsv(Object valor) {
        if (valor == null) {
            return "";
        }

        String texto = valor.toString().replace("\"", "\"\"");
        if (texto.contains(";") || texto.contains("\n") || texto.contains("\r") || texto.contains("\"")) {
            return "\"" + texto + "\"";
        }

        return texto;
    }
}
