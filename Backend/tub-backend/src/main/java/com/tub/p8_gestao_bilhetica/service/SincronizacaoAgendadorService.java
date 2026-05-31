package com.tub.p8_gestao_bilhetica.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.ConfiguracaoIntegracao;
import com.tub.p8_gestao_bilhetica.repository.ConfiguracaoIntegracaoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SincronizacaoAgendadorService {

    private final ConnectionService connectionService;
    private final ProcessadorArmazenamento procesadorArmazenamento;
    private final ConfiguracaoIntegracaoRepository configRepository;
    
    private LocalDateTime ultimaExecucao = null;

    public SincronizacaoAgendadorService(
            ConnectionService connectionService,
            ProcessadorArmazenamento procesadorArmazenamento,
            ConfiguracaoIntegracaoRepository configRepository
    ) {
        this.connectionService = connectionService;
        this.procesadorArmazenamento = procesadorArmazenamento;
        this.configRepository = configRepository;
    }

    // Run check every 30 seconds
    @Scheduled(fixedDelay = 30000)
    public void verificarESincronizar() {
        ConfiguracaoIntegracao config = configRepository.findAll().stream()
                .filter(ConfiguracaoIntegracao::getAtiva)
                .findFirst()
                .orElse(null);

        if (config == null || !config.getAtiva()) {
            return;
        }

        int intervalo = config.getIntervaloMinutos() != null ? config.getIntervaloMinutos() : 5;
        
        if (ultimaExecucao == null || ultimaExecucao.plusMinutes(intervalo).isBefore(LocalDateTime.now())) {
            System.out.println("SincronizacaoAgendadorService: Iniciando importação automática periódica...");
            try {
                executarImportacao();
                ultimaExecucao = LocalDateTime.now();
                System.out.println("SincronizacaoAgendadorService: Importação automática periódica concluída com sucesso.");
            } catch (Exception e) {
                System.err.println("SincronizacaoAgendadorService: Erro na importação periódica: " + e.getMessage());
            }
        }
    }

    private synchronized void executarImportacao() {
        List<Validation> dados = connectionService.obterDadosBilhetica();
        procesadorArmazenamento.processarEGuardarSincronizacao(dados);
    }
}
