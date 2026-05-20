package com.tub.p7_relatorios.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MotorGrafico {

    public Map<String, Object> gerarResumoGrafico(int totalPassageiros, String lotacaoMedia) {
        Map<String, Object> grafico = new LinkedHashMap<>();

        grafico.put("tipoGrafico", "BARRAS");
        grafico.put("descricao", "Resumo visual das métricas operacionais.");
        grafico.put("totalPassageiros", totalPassageiros);
        grafico.put("lotacaoMedia", lotacaoMedia);

        return grafico;
    }
}