package com.tub.service;

import com.tub.model.LotacaoViatura;
import com.tub.repository.LotacaoViaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificacaoLotacaoWorker {

    @Autowired
    private LotacaoViaturaRepository lotacaoRepository;

    @Autowired
    private AlertaLotacaoService alertaService;

    /**
     * Linha 60: Trigger/job de verificação periódica.
     * Este método é executado automaticamente a cada 30 segundos (30000ms).
     */
    @Scheduled(fixedRate = 30000)
    public void verificarLotacaoPeriodica() {
        System.out.println(">>> [JOB] A iniciar verificação periódica de lotação em toda a frota...");

        // 1. Obtém o estado atual de todas as viaturas na base de dados (Linha 56)
        List<LotacaoViatura> viaturas = lotacaoRepository.findAll();

        if (viaturas.isEmpty()) {
            System.out.println(">>> [JOB] Nenhuma viatura encontrada para verificação.");
            return;
        }

        for (LotacaoViatura viatura : viaturas) {
            // 2. Invoca o Motor de Avaliação (Linha 61) para cada viatura.
            // O motor decide se deve gerar um alerta baseado na taxa de ocupação.
            alertaService.verificarEAplicarAlerta(viatura);
        }
        
        System.out.println(">>> [JOB] Verificação de frota concluída com sucesso.");
    }
}