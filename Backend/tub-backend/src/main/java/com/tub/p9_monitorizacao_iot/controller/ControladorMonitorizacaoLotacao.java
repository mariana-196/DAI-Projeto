package com.tub.p9_monitorizacao_iot.controller;

import com.tub.p3_integracao_externa.adapter.InterfaceTelemetriaLotacao;
import com.tub.p3_integracao_externa.model.PassengerCount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("controladorMonitorizacaoLotacaoController")
@RequestMapping("/api/monitorizacao")
@CrossOrigin(origins = "*")
public class ControladorMonitorizacaoLotacao {

    @Autowired
    private InterfaceTelemetriaLotacao wavecomAdapter;

    @Autowired
    private com.tub.p9_monitorizacao_iot.service.SeviceMonitorizacaoLotacao contagemService;

    private int passageirosAtual = 10;
    private boolean sinalAtivo = true;
    private final int CAPACIDADE_MAXIMA = 50;

    @GetMapping("/sincronizar")
    public ResponseEntity<List<PassengerCount>> sincronizarSensores() {
        List<PassengerCount> contagens = wavecomAdapter.getPassengerCounts();

        contagemService.processarContagens(contagens);

        for (PassengerCount c : contagens) {
            this.passageirosAtual += (c.getPassengersIn() - c.getPassengersOut());
        }

        if (this.passageirosAtual < 0) {
            this.passageirosAtual = 0;
        }

        return ResponseEntity.ok(contagens);
    }

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