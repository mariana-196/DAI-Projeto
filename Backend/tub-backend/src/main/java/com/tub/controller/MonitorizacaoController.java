package com.tub.controller;

import com.tub.adapter.WavecomAdapter;
import com.tub.model.PassengerCount;
import com.tub.service.ContagemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitorizacao")
@CrossOrigin(origins = "*")
public class MonitorizacaoController {

    @Autowired
    private WavecomAdapter wavecomAdapter;

    @Autowired
    private ContagemService contagemService; // Injetar a Lógica da Linha 55

    // Estado volátil temporário (Será substituído totalmente pela BD quando descomentares o Service)
    private int passageirosAtual = 10;
    private boolean sinalAtivo = true;
    private final int CAPACIDADE_MAXIMA = 50;

    /**
     * Endpoint unificado para Linha 54 e Linha 55
     */
    @GetMapping("/sincronizar")
    public ResponseEntity<List<PassengerCount>> sincronizarSensores() {
        // 1. O Adapter vai buscar os dados físicos (Linha 54)
        List<PassengerCount> contagens = wavecomAdapter.getPassengerCounts();

        // 2. O Service faz as contas matemáticas e GUARDA NA BASE DE DADOS (Linha 55)
        contagemService.processarContagens(contagens);

        // Atualização da variável local apenas para o endpoint de testes /status continuar a funcionar
        for (PassengerCount c : contagens) {
            this.passageirosAtual += (c.getPassengersIn() - c.getPassengersOut());
        }
        if (this.passageirosAtual < 0) this.passageirosAtual = 0;

        return ResponseEntity.ok(contagens);
    }

    // ... (Os métodos /status e /atualizar mantêm-se iguais ao que fizemos no passo anterior) ...
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("passageiros", this.passageirosAtual);
        status.put("sinal", this.sinalAtivo);
        status.put("capacidade", this.CAPACIDADE_MAXIMA);
        status.put("taxaOcupacao", (double) this.passageirosAtual / CAPACIDADE_MAXIMA * 100);
        return status;
    }
}