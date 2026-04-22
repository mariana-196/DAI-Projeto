package com.tub.controller;

import com.tub.p6_auditoria.model.ConfiguracaoNotificacoes;
import com.tub.p6_auditoria.service.NotificationConfigService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria/config-notificacoes")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class NotificationConfigController {

    private final ConfiguracaoNotificacoes notificationConfigService;

    public NotificationConfigController(ConfiguracaoNotificacoes notificationConfigService) {
        this.notificationConfigService = notificationConfigService;
    }

    @GetMapping
    public ConfiguracaoNotificacoes obterConfiguracao() {
        return notificationConfigService.obterConfiguracaoAtual();
    }

    @PutMapping
    public ConfiguracaoNotificacoes atualizarConfiguracao(@RequestBody ConfiguracaoNotificacoes configuracao) {
        return notificationConfigService.atualizarConfiguracao(configuracao);
    }
}