package com.tub.service;

import com.tub.model.ConfiguracaoNotificacoes;
import com.tub.repository.ConfiguracaoNotificacoesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationConfigService {

    private final ConfiguracaoNotificacoesRepository configuracaoNotificacoesRepository;

    public NotificationConfigService(ConfiguracaoNotificacoesRepository configuracaoNotificacoesRepository) {
        this.configuracaoNotificacoesRepository = configuracaoNotificacoesRepository;
    }

    public ConfiguracaoNotificacoes obterConfiguracaoAtual() {
        List<ConfiguracaoNotificacoes> configuracoes = configuracaoNotificacoesRepository.findAll();

        if (configuracoes.isEmpty()) {
            ConfiguracaoNotificacoes configDefault = new ConfiguracaoNotificacoes(
                    true,
                    "EMAIL",
                    "INFO",
                    "admin@tub.pt"
            );
            return configuracaoNotificacoesRepository.save(configDefault);
        }

        return configuracoes.get(0);
    }

    public ConfiguracaoNotificacoes atualizarConfiguracao(ConfiguracaoNotificacoes novaConfiguracao) {
        ConfiguracaoNotificacoes atual = obterConfiguracaoAtual();

        if (novaConfiguracao.getEmailDestinoDefault() != null && !novaConfiguracao.getEmailDestinoDefault().isBlank()) {
            if (!emailValido(novaConfiguracao.getEmailDestinoDefault())) {
                throw new RuntimeException("Email destino default inválido.");
            }
        }

        atual.setNotificacoesAtivas(novaConfiguracao.getNotificacoesAtivas());
        atual.setCanalDefault(novaConfiguracao.getCanalDefault());
        atual.setSeveridadeMinimaGlobal(novaConfiguracao.getSeveridadeMinimaGlobal());
        atual.setEmailDestinoDefault(novaConfiguracao.getEmailDestinoDefault());

        return configuracaoNotificacoesRepository.save(atual);
    }

    private boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}