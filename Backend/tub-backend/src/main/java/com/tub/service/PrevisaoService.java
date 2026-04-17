package com.tub.service;

import org.springframework.stereotype.Service;

@Service
public class PrevisaoService {

    // Tempo médio padrão entre paragens em minutos (Pode ser ajustado)
    private static final double TEMPO_MEDIO_PARAGEM = 2.5;

    /**
     * UC 6.3.1.c - Calcular ETA
     * Calcula o tempo estimado com base no número de paragens restantes.
     */
    public double calcularETAMedia(int paragensRestantes) {
        if (paragensRestantes <= 0) {
            return 0.0000;
        }
        
        // Lógica de Média: Paragens * Tempo Médio
        return paragensRestantes * TEMPO_MEDIO_PARAGEM;
    }

    /**
     * Verifica se o serviço de lógica está operacional
     */
    public boolean isStatusOk() {
        return true; 
    }
}
