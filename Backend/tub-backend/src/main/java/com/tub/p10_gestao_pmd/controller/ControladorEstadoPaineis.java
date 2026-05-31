package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.MensagemPMD;
import com.tub.p10_gestao_pmd.repository.MensagemPMDRepository;
import com.tub.p10_gestao_pmd.service.ServicoPaineis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paineis")
@CrossOrigin(origins = "*")
public class ControladorEstadoPaineis {

    @Autowired
    private ServicoPaineis painelService;

    @Autowired
    private MensagemPMDRepository mensagemPMDRepository;

    /**
     * Retorna a lista e o estado atual dos painéis.
     */
    @GetMapping
    public ResponseEntity<List<DisplayPanel>> listarPaineis() {
        List<DisplayPanel> paineis = painelService.listarTodosOsPaineis();
        return ResponseEntity.ok(paineis);
    }

    /**
     * Retorna o histórico de todas as mensagens emitidas.
     */
    @GetMapping("/historico")
    public ResponseEntity<List<MensagemPMD>> consultarHistorico() {
        // Obter mensagens cujo estado seja "ATIVA" (já exibidas/enviadas) ou "INATIVA" (limpas do painel)
        // Ordenado por data/hora mais recente
        List<MensagemPMD> historico = mensagemPMDRepository.findAll().stream()
                .filter(m -> "ATIVA".equals(m.getEstado()) || "INATIVA".equals(m.getEstado()))
                .sorted((m1, m2) -> m2.getDataCriacao().compareTo(m1.getDataCriacao()))
                .toList();
        return ResponseEntity.ok(historico);
    }
}
