package com.tub.service;

import com.tub.model.PoliticasAuditoria;
import com.tub.repository.PoliticasAuditoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditPolicyService {

    private final PoliticasAuditoriaRepository politicasAuditoriaRepository;

    public AuditPolicyService(PoliticasAuditoriaRepository politicasAuditoriaRepository) {
        this.politicasAuditoriaRepository = politicasAuditoriaRepository;
    }

    public PoliticasAuditoria obterPoliticaAtual() {
        List<PoliticasAuditoria> politicas = politicasAuditoriaRepository.findAll();

        if (politicas.isEmpty()) {
            PoliticasAuditoria politicaDefault = new PoliticasAuditoria(
                    "INFO",
                    90,
                    false,
                    null
            );
            return politicasAuditoriaRepository.save(politicaDefault);
        }

        return politicas.get(0);
    }

    public PoliticasAuditoria atualizarPolitica(PoliticasAuditoria novaPolitica) {
        PoliticasAuditoria politicaAtual = obterPoliticaAtual();

        politicaAtual.setNivelMinimo(novaPolitica.getNivelMinimo());
        politicaAtual.setDiasRetencao(novaPolitica.getDiasRetencao());
        politicaAtual.setNotificacoesAtivas(novaPolitica.getNotificacoesAtivas());
        politicaAtual.setEmailNotificacao(novaPolitica.getEmailNotificacao());
        politicaAtual.setDataAtualizacao(LocalDateTime.now());

        return politicasAuditoriaRepository.save(politicaAtual);
    }
}