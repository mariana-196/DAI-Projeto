package com.tub.service;

import com.tub.model.Utilizador;
import com.tub.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;

    public AuthService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    public Utilizador autenticar(String email, String password) {
        Optional<Utilizador> op = utilizadorRepository.findByEmail(email);

        if (op.isEmpty()) {
            return null;
        }

        Utilizador utilizador = op.get();

        if (!utilizador.isAtivo()) {
            return null;
        }

        if (!utilizador.getPassword().equals(password)) {
            return null;
        }

        return utilizador;
    }
}