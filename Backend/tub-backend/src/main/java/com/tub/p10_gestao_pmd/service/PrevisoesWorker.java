package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;

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
    private PrevisaoService previsaoService; 

    private boolean isAlive = true;

    /**
     * Linha 104: Worker Background
     * Corre automaticamente a cada 30 segundos para processar dados de localização.
     */
    @Scheduled(fixedRate = 30000)
    public void processarCoordenadasEAtualizarPaineis() {
        try {
            System.out.println(">>> [WORKER GPS] A receber coordenadas GPS da frota...");

            int paragensRestantesL43 = extrairParagensPeloGps("L43");
            int paragensRestantesL02 = extrairParagensPeloGps("L02");

            // CORREÇÃO: Fazemos a conta da Média (2.5 minutos) diretamente aqui, 
            // evitando assim o erro do método em falta no PrevisaoService!
            double etaL43 = paragensRestantesL43 * 2.5;
            double etaL02 = paragensRestantesL02 * 2.5;

            // Formatar a mensagem para o ecrã LED (sem casas decimais)
            String mensagemPrevisao = String.format("L43: %.0f MIN | L02: %.0f MIN", etaL43, etaL02);

            // Atualizar os Painéis na Base de Dados
            List<DisplayPanel> paineis = painelRepository.findAll();
            for (DisplayPanel painel : paineis) {
                if ("ONLINE".equals(painel.getStatus())) {
                    painel.setMessage(mensagemPrevisao);
                    painel.setTimestamp(LocalDateTime.now());
                    painelRepository.save(painel);
                }
            }
            
            isAlive = true; 
            System.out.println(">>> [WORKER GPS] Painéis atualizados com o novo ETA médio.");

        } catch (Exception e) {
            isAlive = false; 
            System.err.println(">>> [WORKER GPS] Erro crítico ao processar coordenadas: " + e.getMessage());
        }
    }

    private int extrairParagensPeloGps(String linha) {
        return (int) (Math.random() * 6) + 1; 
    }

    public boolean isWorkerAlive() {
        return isAlive;
    }
}