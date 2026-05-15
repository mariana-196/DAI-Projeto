package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.RelatorioDTO;
import com.tub.p7_relatorios.dto.RelatorioFiltroDTO;
import com.tub.p7_relatorios.service.ExportacaoService;
import com.tub.p7_relatorios.service.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ExportacaoService exportacaoService;

    public RelatorioController(RelatorioService relatorioService, ExportacaoService exportacaoService) {
        this.relatorioService = relatorioService;
        this.exportacaoService = exportacaoService;
    }

    @PostMapping("/auditoria")
    public ResponseEntity<RelatorioDTO> gerarRelatorioAuditoria(@RequestBody RelatorioFiltroDTO filtro) {
        RelatorioDTO relatorio = relatorioService.gerarRelatorioAuditoria(filtro);
        return ResponseEntity.ok(relatorio);
    }

    @PostMapping("/operacional")
    public ResponseEntity<RelatorioDTO> gerarRelatorioOperacional(@RequestBody RelatorioFiltroDTO filtro) {
        RelatorioDTO relatorio = relatorioService.gerarRelatorioOperacional(filtro);
        return ResponseEntity.ok(relatorio);
    }

    @PostMapping("/exportar/csv")
    public ResponseEntity<String> exportarCSV(@RequestBody RelatorioFiltroDTO filtro) {
        RelatorioDTO relatorio;

        if ("AUDITORIA".equalsIgnoreCase(filtro.getTipoRelatorio())) {
            relatorio = relatorioService.gerarRelatorioAuditoria(filtro);
        } else {
            relatorio = relatorioService.gerarRelatorioOperacional(filtro);
        }

        String csv = exportacaoService.gerarCSV(relatorio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarErroValidacao(IllegalArgumentException erro) {
        return ResponseEntity.badRequest().body(erro.getMessage());
    }
}