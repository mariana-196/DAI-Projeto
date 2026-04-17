package com.tub.controller;

import com.tub.model.ConfiguracaoNotificacoes;
import com.tub.service.NotificationConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria/config-notificacoes")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class NotificationConfigController {

    private final NotificationConfigService notificationConfigService;

    public NotificationConfigController(NotificationConfigService notificationConfigService) {
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