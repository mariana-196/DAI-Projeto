package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.model.PrevisaoChegada;
import com.tub.p10_gestao_pmd.service.PrevisaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/previsoes")
@CrossOrigin(origins = "*")
public class ControloPrevisao {

    
    @Autowired
    private PrevisaoService previsaoService;

    /**
     * UC 6.3.1 - Calcular Tempo Estimado
     * O Controller só recebe o pedido e manda o Service fazer as contas.
     */
    @PostMapping("/calcular")
    public ResponseEntity<PrevisaoChegada> processarNovaPrevisao(
            @RequestParam Long viaturaId, 
            @RequestParam Long paragemId, 
            @RequestParam int paragensRestantes) {
        
        // A magia matemática e de base de dados acontece toda dentro do Service agora
        PrevisaoChegada previsao = previsaoService.calcularEGuardarPrevisao(viaturaId, paragemId, paragensRestantes);

        return ResponseEntity.ok(previsao);
    }

    /**
     * Consulta de previsões para o ecrã dos passageiros
     */
    @GetMapping("/consulta/{paragemId}")
    public ResponseEntity<List<PrevisaoChegada>> obterPrevisoesPorParagem(@PathVariable Long paragemId) {
        
        List<PrevisaoChegada> lista = previsaoService.obterPrevisoesDaParagem(paragemId);
                
        return ResponseEntity.ok(lista);
    }

    /**
     * Endpoint de Verificação (Health Check - ctrlPrevisaoAlive)
     */
    @GetMapping("/alive")
    public ResponseEntity<String> verificarEstado() {
        return ResponseEntity.ok("UP - Controlador de Previsão Operacional");
    }
}