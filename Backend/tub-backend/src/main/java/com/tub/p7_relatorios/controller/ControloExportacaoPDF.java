package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.FicheiroPDF;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/relatorios/exportar/pdf")
@CrossOrigin(origins = "*")
public class ControloExportacaoPDF {

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

    @PostMapping
    public ResponseEntity<FicheiroPDF> exportarPDF() {
        String conteudoSimulado = "PDF simulado do relatório do sistema.";
        String base64 = Base64.getEncoder().encodeToString(conteudoSimulado.getBytes(StandardCharsets.UTF_8));

        FicheiroPDF ficheiroPDF = new FicheiroPDF(
                "relatorio_sistema.pdf",
                base64,
                "application/pdf"
        );

        auditService.registar(
                getExecutorEmail(),
                "EXPORTAR_RELATORIO",
                "Relatórios",
                getExecutorIp(),
                "INFO",
                "Relatório exportado com sucesso em formato PDF."
        );

        return ResponseEntity.ok(ficheiroPDF);
    }
}