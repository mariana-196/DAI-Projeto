package com.tub.p9_monitorizacao_iot.controller;

import com.tub.p3_integracao_externa.adapter.InterfaceTelemetriaLotacao;
import com.tub.p3_integracao_externa.model.PassengerCount;
import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;
import com.tub.p9_monitorizacao_iot.repository.LotacaoViaturaRepository;
import com.tub.p10_gestao_pmd.model.Viatura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController("controladorMonitorizacaoLotacaoController")
@RequestMapping("/api/monitorizacao")
@CrossOrigin(origins = "*")
public class ControladorMonitorizacaoLotacao {

    @Autowired
    private InterfaceTelemetriaLotacao wavecomAdapter;

    @Autowired
    private com.tub.p9_monitorizacao_iot.service.SeviceMonitorizacaoLotacao contagemService;

    @Autowired
    private LotacaoViaturaRepository lotacaoViaturaRepository;

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

    @GetMapping("/status-geral")
    public ResponseEntity<List<Map<String, Object>>> getStatusGeral() {
        List<EstadoOcupacaoViatura> estados = lotacaoViaturaRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (EstadoOcupacaoViatura estado : estados) {
            Map<String, Object> item = new HashMap<>();
            Viatura v = estado.getViatura();

            item.put("id", v != null ? v.getCodigo() : estado.getId());
            item.put("linha", estado.getLinha());
            item.put("passageiros", estado.getPassageirosAtuais());
            item.put("capacidade", v != null ? v.getCapacidadeMaxima() : CAPACIDADE_MAXIMA);
            item.put("sinal", estado.isSinalAtivo());

            resposta.add(item);
        }

        return ResponseEntity.ok(resposta);
    }
}