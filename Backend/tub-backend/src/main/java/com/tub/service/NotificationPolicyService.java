package com.tub.service;

import com.tub.model.RegraNotificacao;
import com.tub.repository.RegraNotificacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationPolicyService {

    private final RegraNotificacaoRepository regraNotificacaoRepository;

    public NotificationPolicyService(RegraNotificacaoRepository regraNotificacaoRepository) {
        this.regraNotificacaoRepository = regraNotificacaoRepository;
    }

    public List<RegraNotificacao> listarRegras() {
        return regraNotificacaoRepository.findAll();
    }

    public RegraNotificacao criarRegra(RegraNotificacao regra) {
        if (!emailValido(regra.getDestinatario())) {
            throw new RuntimeException("Destinatário inválido.");
        }

        return regraNotificacaoRepository.save(regra);
    }

    public RegraNotificacao atualizarRegra(Long id, RegraNotificacao novaRegra) {
        RegraNotificacao regra = regraNotificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regra não encontrada"));

        if (!emailValido(novaRegra.getDestinatario())) {
            throw new RuntimeException("Destinatário inválido.");
        }

        regra.setTipoEvento(novaRegra.getTipoEvento());
        regra.setDestinatario(novaRegra.getDestinatario());
        regra.setAtiva(novaRegra.getAtiva());
        regra.setCanal(novaRegra.getCanal());
        regra.setSeveridadeMinima(novaRegra.getSeveridadeMinima());

        return regraNotificacaoRepository.save(regra);
    }

    public void apagarRegra(Long id) {
        regraNotificacaoRepository.deleteById(id);
    }

    private boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}