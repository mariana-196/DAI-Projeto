package com.tub.service;

import com.tub.model.SessaoAutenticada;
import com.tub.model.Utilizador;
import com.tub.repository.SessaoAutenticadaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AutorizacaoService {

    private final SessaoAutenticadaRepository sessaoAutenticadaRepository;

    public AutorizacaoService(SessaoAutenticadaRepository sessaoAutenticadaRepository) {
        this.sessaoAutenticadaRepository = sessaoAutenticadaRepository;
    }

    public Utilizador obterUtilizadorPorToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        Optional<SessaoAutenticada> op = sessaoAutenticadaRepository.findByToken(token);

        if (op.isEmpty()) {
            return null;
        }

        SessaoAutenticada sessao = op.get();

        if (!sessao.isAtiva()) {
            return null;
        }

        if (sessao.getDataExpiracao() != null && sessao.getDataExpiracao().isBefore(LocalDateTime.now())) {
            return null;
        }

        return sessao.getUtilizador();
    }

    public boolean eAdmin(String token) {
        Utilizador utilizador = obterUtilizadorPorToken(token);

        if (utilizador == null) {
            return false;
        }

        return "ADMIN".equalsIgnoreCase(utilizador.getCargo());
    }
}