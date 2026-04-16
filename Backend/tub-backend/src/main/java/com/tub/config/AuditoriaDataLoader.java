package com.tub.config;

import com.tub.model.RegistoAuditoria;
import com.tub.repository.RegistoAuditoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaDataLoader implements CommandLineRunner {

    private final RegistoAuditoriaRepository registoAuditoriaRepository;

    public AuditoriaDataLoader(RegistoAuditoriaRepository registoAuditoriaRepository) {
        this.registoAuditoriaRepository = registoAuditoriaRepository;
    }

    @Override
    public void run(String... args) {
        if (registoAuditoriaRepository.count() == 0) {
            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "admin@tub.pt",
                    "Início de Sessão",
                    "Autenticação",
                    "192.168.1.45",
                    "INFO",
                    "Login com sucesso"
            ));

            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "operador2@tub.pt",
                    "Falha de Autenticação",
                    "Autenticação",
                    "85.240.12.5",
                    "AVISO",
                    "Password incorreta"
            ));

            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "admin@tub.pt",
                    "Exportação (CSV)",
                    "Bilhética",
                    "192.168.1.45",
                    "CRÍTICO",
                    "Exportação de dados"
            ));
        }
    }
}