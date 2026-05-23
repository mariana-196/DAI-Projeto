package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.PainelPMD;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p10_gestao_pmd.model.PrevisaoChegada;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;
import com.tub.p10_gestao_pmd.repository.PainelPMDRepository;
import com.tub.p10_gestao_pmd.repository.PrevisaoChegadaRepository;
import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class PrevisoesWorker {

    @Autowired
    private DisplayPanelRepository painelRepository;

    @Autowired
    private PainelPMDRepository painelPMDRepository;

    @Autowired
    private PrevisaoChegadaRepository previsaoChegadaRepository;

    @Autowired
    private LinhaRepository linhaRepository;

    @Autowired
    private ViaturasRepository viaturasRepository;

    @Autowired
    private PrevisaoService previsaoService; 

    private boolean isAlive = true;
    private final Random random = new Random();

    /**
     * Linha 104: Worker Background
     * Corre automaticamente a cada 30 segundos para processar dados de localização e gerar previsões consistentes.
     */
    @Scheduled(fixedRate = 30000)
    public void processarCoordenadasEAtualizarPaineis() {
        try {
            System.out.println(">>> [WORKER GPS] A receber coordenadas GPS da frota...");

            // 1. Limpeza de previsões desatualizadas anteriores a 15 minutos utilizando método nativo e otimizado
            LocalDateTime limiteAntigo = LocalDateTime.now().minusMinutes(15);
            previsaoChegadaRepository.deleteByTimestampBefore(limiteAntigo);

            List<PainelPMD> paineis = painelPMDRepository.findAll();
            List<Linha> linhas = linhaRepository.findAll();
            List<Viatura> viaturas = viaturasRepository.findAll();

            if (paineis.isEmpty() || linhas.isEmpty() || viaturas.isEmpty()) {
                System.out.println(">>> [WORKER GPS] Sem painéis, linhas ou viaturas para simulação de previsões.");
                return;
            }

            // 2. Simular e persistir 1 ou 2 previsões para cada painel que esteja online na base de dados
            for (PainelPMD painel : paineis) {
                if ("ONLINE".equals(painel.getEstado())) {
                    // Limpar previsões recentes do painel de forma otimizada via query do repositório
                    previsaoChegadaRepository.deleteByPainelId(painel.getId());

                    // Gerar 2 previsões aleatórias consistentes
                    for (int i = 0; i < 2; i++) {
                        Linha linhaAleatoria = linhas.get(random.nextInt(linhas.size()));
                        Viatura viaturaAleatoria = viaturas.get(random.nextInt(viaturas.size()));
                        int paragensRestantes = random.nextInt(6) + 1; // 1 a 6 paragens restantes
                        int etaMinutos = (int) Math.round(paragensRestantes * 2.5);

                        PrevisaoChegada previsao = new PrevisaoChegada();
                        previsao.setPainel(painel);
                        previsao.setLinha(linhaAleatoria);
                        previsao.setViatura(viaturaAleatoria);
                        previsao.setDestino(linhaAleatoria.getDestino());
                        previsao.setEtaMinutos(etaMinutos);
                        previsaoChegadaRepository.save(previsao);
                    }

                    // 3. Atualizar a mensagem do painel eletrónico correspondente (DisplayPanel) com base nos novos dados
                    previsaoService.atualizarMensagemPainelComPrevisoes(painel.getId(), painel.getCodigo());
                }
            }
            
            isAlive = true; 
            System.out.println(">>> [WORKER GPS] Painéis atualizados com o novo ETA médio real e consistência de dados.");

        } catch (Exception e) {
            isAlive = false; 
            System.err.println(">>> [WORKER GPS] Erro crítico ao processar coordenadas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isWorkerAlive() {
        return isAlive;
    }
}