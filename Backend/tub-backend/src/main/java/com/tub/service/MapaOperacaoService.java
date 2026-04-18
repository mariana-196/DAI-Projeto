package com.tub.service;

import com.tub.model.ContextoElementoMapa;
import com.tub.model.EventoGeografico;
import org.springframework.stereotype.Service;

@Service
public class MapaOperacaoService {

    // Simulação da Linha 69: Detetar eventos geográficos
    public EventoGeografico processarGeofencing(Long viaturaId, double lat, double lng) {
        // Lógica simplificada: Se estiver fora de um raio X, gera evento
        if (lat > 41.5600) { 
            // Repara aqui: adicionei "1L" como o ID do evento para não dar erro
            return new EventoGeografico(1L, viaturaId, "DESVIO_ROTA", "Viatura fora do corredor planeado.");
        }
        return null;
    }

    /**
     * Linha 67: Serviço que recolhe os dados do elemento.
     * Na vida real, iria à BD cruzar dados de GPS (Wavecom) com Lotação (IoT).
     */
    public ContextoElementoMapa obterContextoViatura(String viaturaId) {
        // Simulação de recolha de contexto
        return new ContextoElementoMapa(
            viaturaId,
            "Linha 43",
            "Em circulação",
            45.5, // Velocidade
            60,   // Lotação atual %
            "Ativo"
        );
    }
}