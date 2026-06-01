package com.tub.p6_auditoria.service;

import com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria;
import com.tub.p6_auditoria.repository.PoliticasAuditoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("servicoPoliticasSistema")
public class ControloPoliticasSistema {

    private final PoliticasAuditoriaRepository politicasAuditoriaRepository;

    public ControloPoliticasSistema(PoliticasAuditoriaRepository politicasAuditoriaRepository) {
        this.politicasAuditoriaRepository = politicasAuditoriaRepository;
    }

    public EntidadeConfiguracoesAuditoria obterPoliticaAtual() {
        List<EntidadeConfiguracoesAuditoria> politicas = politicasAuditoriaRepository.findAll();

        if (politicas.isEmpty()) {
            EntidadeConfiguracoesAuditoria politicaDefault = new EntidadeConfiguracoesAuditoria(
                    "INFO",
                    365,
                    false,
                    null
            );
            return politicasAuditoriaRepository.save(politicaDefault);
        }

        return politicas.get(0);
    }

    public EntidadeConfiguracoesAuditoria atualizarPolitica(EntidadeConfiguracoesAuditoria novaPolitica) {
        EntidadeConfiguracoesAuditoria politicaAtual = obterPoliticaAtual();

        if (novaPolitica.getDiasRetencao() == null || novaPolitica.getDiasRetencao() < 365) {
            throw new RuntimeException("A retenção mínima dos logs é 365 dias.");
        }

        if (novaPolitica.getEmailNotificacao() != null && !novaPolitica.getEmailNotificacao().isBlank()) {
            if (!emailValido(novaPolitica.getEmailNotificacao())) {
                throw new RuntimeException("Email de notificação inválido.");
            }
        }

        politicaAtual.setNivelMinimo(novaPolitica.getNivelMinimo());
        politicaAtual.setDiasRetencao(novaPolitica.getDiasRetencao());
        politicaAtual.setNotificacoesAtivas(novaPolitica.getNotificacoesAtivas());
        politicaAtual.setEmailNotificacao(novaPolitica.getEmailNotificacao());
        politicaAtual.setDataAtualizacao(LocalDateTime.now());

        return politicasAuditoriaRepository.save(politicaAtual);
    }

    private boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}