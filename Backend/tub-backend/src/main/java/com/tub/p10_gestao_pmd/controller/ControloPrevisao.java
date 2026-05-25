package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.model.PrevisaoChegada;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.PainelPMD;
import com.tub.p10_gestao_pmd.service.PrevisaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.util.List;

@RestController
@RequestMapping("/api/previsoes")
@CrossOrigin(origins = "*")
public class ControloPrevisao {

    @Autowired
    private PrevisaoService previsaoService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ControloConsultaAuditoria auditService;

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Dispositivo/Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    @PostMapping("/calcular")
    public ResponseEntity<PrevisaoChegada> processarNovaPrevisao(
            @RequestParam Long viaturaId, 
            @RequestParam Long painelId, 
            @RequestParam Long linhaId,
            @RequestParam String destino,
            @RequestParam int paragensRestantes) {
        
        PrevisaoChegada previsao = previsaoService.calcularEGuardarPrevisao(viaturaId, painelId, linhaId, destino, paragensRestantes);

        auditService.registar(
                getExecutorEmail(),
                "ATUALIZAR_PAINEL",
                "Painéis PMD/DMS",
                getExecutorIp(),
                "INFO",
                "Previsão de chegada atualizada para viatura #" + viaturaId + " no painel #" + painelId + " (Linha: " + linhaId + ", Destino: " + destino + ", Paragens restantes: " + paragensRestantes + ")."
        );

        return ResponseEntity.ok(previsao);
    }

    @GetMapping("/consulta/{painelId}")
    public ResponseEntity<List<PrevisaoChegada>> obterPrevisoesPorParagem(@PathVariable Long painelId) {
        List<PrevisaoChegada> lista = previsaoService.obterPrevisoesDaParagem(painelId);      
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/alive")
    public ResponseEntity<String> verificarEstado() {
        return ResponseEntity.ok("UP - Controlador de Previsão Operacional");
    }

    @GetMapping("/linhas")
    public ResponseEntity<List<Linha>> obterLinhas() {
        return ResponseEntity.ok(previsaoService.obterTodasAsLinhas());
    }

    @GetMapping("/paineis-pmd")
    public ResponseEntity<List<PainelPMD>> obterPaineisPMD() {
        return ResponseEntity.ok(previsaoService.obterTodosOsPaineisPMD());
    }
}