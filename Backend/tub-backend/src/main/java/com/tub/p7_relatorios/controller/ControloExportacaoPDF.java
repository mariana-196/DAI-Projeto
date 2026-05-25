package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.FicheiroPDF;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/relatorios/exportar/pdf")
@CrossOrigin(origins = "*")
public class ControloExportacaoPDF {

    @PostMapping
    public ResponseEntity<FicheiroPDF> exportarPDF() {
        String conteudoSimulado = "PDF simulado do relatório do sistema.";
        String base64 = Base64.getEncoder().encodeToString(conteudoSimulado.getBytes(StandardCharsets.UTF_8));

        FicheiroPDF ficheiroPDF = new FicheiroPDF(
                "relatorio_sistema.pdf",
                base64,
                "application/pdf"
        );

        return ResponseEntity.ok(ficheiroPDF);
    }
}