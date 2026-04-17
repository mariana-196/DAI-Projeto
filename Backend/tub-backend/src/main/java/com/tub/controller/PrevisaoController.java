package com.tub.controller;

import com.tub.service.PrevisaoService;
import com.tub.service.PrevisoesWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Linha 105: ctrlPrevisaoAlive
 * Controlador responsável por expor a saúde do sistema de previsões e os cálculos de ETA.
 */
@RestController
@RequestMapping("/api/previsoes")
@CrossOrigin(origins = "*")
public class PrevisaoController {

    @Autowired
    private PrevisaoService previsaoService;

    @Autowired
    private PrevisoesWorker previsoesWorker;

    /**
     * UC 6.3.1.c - Endpoint 'Alive'
     * Verifica se o serviço de cálculo e o worker de GPS estão operacionais.
     */
    @GetMapping("/alive")
    public ResponseEntity<Map<String, Object>> getPrevisaoAlive() {
        Map<String, Object> response = new HashMap<>();
        
        // Verifica a saúde dos componentes das Linhas 104 e 105
        boolean logicStatus = previsaoService.isStatusOk();
        boolean workerStatus = previsoesWorker.isWorkerAlive();
        
        response.put("status", (logicStatus && workerStatus) ? "UP" : "DOWN");
        response.put("logic_service_alive", logicStatus);
        response.put("gps_worker_alive", workerStatus);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * UC 6.3.1.c - Calcular ETA
     * Permite testar ou consultar a estimativa de chegada com base na média.
     * O resultado é devolvido com 4 casas decimais e utiliza o ponto como separador.
     */
    @GetMapping("/estimar")
    public ResponseEntity<Map<String, Object>> estimarChegada(@RequestParam int paragens) {
        double eta = previsaoService.calcularETAMedia(paragens);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("paragens_restantes", paragens);
        
        // Formata o valor para 4 casas decimais usando o ponto (.)
        String etaFormatado = String.format("%.4f", eta).replace(",", ".");
        resultado.put("eta_estimado_minutos", etaFormatado);
        
        return ResponseEntity.ok(resultado);
    }
}
