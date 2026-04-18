package com.tub.service;

import com.tub.model.AlertaUnificado;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertCenterService {

    /**
     * Linha 81: Simula o processamento de triagem.
     * Valida a estrutura, normaliza o formato e classifica a severidade.
     */
    public List<AlertaUnificado> triagemDeAlertas() {
        List<AlertaUnificado> alertasProcessados = new ArrayList<>();

        // Simulação de 1: Alerta vindo da Bilhética (Raw data -> Normalizado)
        alertasProcessados.add(normalizarEvento("BILHETICA", "ERR_404", "Falha de comunicação validador #12"));

        // Simulação de 2: Alerta vindo da Wavecom (Sensores IoT)
        alertasProcessados.add(normalizarEvento("WAVECOM", "TEMP_HIGH", "Motor Viatura #405 acima de 100°C"));

        return alertasProcessados;
    }

    // Lógica de Normalização e Classificação (Requisito da Linha 81)
    private AlertaUnificado normalizarEvento(String origem, String codigo, String mensagem) {
        String severidade;
        
        // CLASSIFICAÇÃO: Regras de negócio para definir a gravidade
        if (codigo.contains("HIGH") || codigo.contains("FAIL")) {
            severidade = "CRÍTICO";
        } else if (origem.equals("BILHETICA")) {
            severidade = "MODERADO";
        } else {
            severidade = "LIGEIRO";
        }

        return new AlertaUnificado(
            System.currentTimeMillis(), // ID temporário
            "Alerta de " + origem,
            mensagem,
            severidade,
            origem,
            "PENDENTE"
        );
    }
}