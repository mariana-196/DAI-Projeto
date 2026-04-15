package com.tub.service;

import com.tub.model.Utilizador;
import com.tub.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;
    private final AuditService auditService;

    public AuthService(UtilizadorRepository utilizadorRepository, AuditService auditService) {
        this.utilizadorRepository = utilizadorRepository;
        this.auditService = auditService;
    }

    public Utilizador autenticar(String email, String password) {
        Optional<Utilizador> op = utilizadorRepository.findByEmail(email);

        if (op.isEmpty()) {
            auditService.registar(
                    email,
                    "Falha de Autenticação",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Utilizador não encontrado"
            );
            return null;
        }

        Utilizador utilizador = op.get();

        if (!utilizador.isAtivo()) {
            auditService.registar(
                    email,
                    "Falha de Autenticação",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Utilizador inativo"
            );
            return null;
        }

        if (!utilizador.getPassword().equals(password)) {
            auditService.registar(
                    email,
                    "Falha de Autenticação",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Password incorreta"
            );
            return null;
        }

        auditService.registar(
                email,
                "Início de Sessão",
                "Autenticação",
                "127.0.0.1",
                "INFO",
                "Login com sucesso"
        );

        return utilizador;
    }
}