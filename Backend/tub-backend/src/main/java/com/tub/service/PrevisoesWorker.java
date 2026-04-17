package com.tub.service;

import com.tub.model.DisplayPanel;
import com.tub.repository.DisplayPanelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PrevisoesWorker {

    @Autowired
    private DisplayPanelRepository painelRepository;

    @Autowired
    private PrevisaoService previsaoService; // Injeta o motor de cálculo da Linha 105

    // Variável para monitorizar se o worker está saudável (workerPrevisoesAlive)
    private boolean isAlive = true;

    /**
     * Linha 104: Worker Background
     * Corre automaticamente a cada 30 segundos para processar dados de localização.
     */
    @Scheduled(fixedRate = 30000)
    public void processarCoordenadasEAtualizarPaineis() {
        try {
            System.out.println(">>> [WORKER GPS] A receber coordenadas GPS da frota...");

            // 1. "Receber coordenadas GPS" (Simulado para o requisito)
            // Na vida real, o GPS indica a que distância o autocarro está da paragem
            int paragensRestantesL43 = extrairParagensPeloGps("L43");
            int paragensRestantesL02 = extrairParagensPeloGps("L02");

            // 2. Usar a lógica de "Média" da Linha 105 para calcular o ETA
            double etaL43 = previsaoService.calcularETAMedia(paragensRestantesL43);
            double etaL02 = previsaoService.calcularETAMedia(paragensRestantesL02);

            // Formatar a mensagem para o ecrã LED (sem casas decimais para ser legível no painel)
            String mensagemPrevisao = String.format("L43: %.0f MIN | L02: %.0f MIN", etaL43, etaL02);

            // 3. Atualizar os Painéis na Base de Dados
            List<DisplayPanel> paineis = painelRepository.findAll();
            for (DisplayPanel painel : paineis) {
                // Só atualizamos os painéis que estão a funcionar
                if ("ONLINE".equals(painel.getStatus())) {
                    painel.setMessage(mensagemPrevisao);
                    painel.setTimestamp(LocalDateTime.now());
                    painelRepository.save(painel);
                }
            }
            
            isAlive = true; // O Worker completou o ciclo com sucesso
            System.out.println(">>> [WORKER GPS] Painéis atualizados com o novo ETA médio.");

        } catch (Exception e) {
            isAlive = false; // Se a BD for abaixo ou houver erro, o status muda
            System.err.println(">>> [WORKER GPS] Erro crítico ao processar coordenadas: " + e.getMessage());
        }
    }

    /**
     * Simula a conversão de Latitude/Longitude em "Paragens Restantes"
     */
    private int extrairParagensPeloGps(String linha) {
        // Gera um número aleatório entre 1 e 6 paragens de distância
        return (int) (Math.random() * 6) + 1; 
    }

    /**
     * Método para o ctrlPrevisaoAlive consultar
     */
    public boolean isWorkerAlive() {
        return isAlive;
    }
}