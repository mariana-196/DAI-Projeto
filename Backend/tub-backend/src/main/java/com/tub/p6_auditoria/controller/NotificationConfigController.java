package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.ConfiguracaoNotificacoes;
import com.tub.p6_auditoria.service.NotificationConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria/config-notificacoes")
@CrossOrigin(origins = "*")
public class NotificationConfigController {

    private final NotificationConfigService notificationConfigService;

    public NotificationConfigController(NotificationConfigService notificationConfigService) {
        this.notificationConfigService = notificationConfigService;
    }

    @GetMapping
    public ConfiguracaoNotificacoes obterConfiguracaoAtual() {
        return notificationConfigService.obterConfiguracaoAtual();
    }

    @PutMapping
    public ConfiguracaoNotificacoes atualizarConfiguracao(@RequestBody ConfiguracaoNotificacoes configuracao) {
        return notificationConfigService.atualizarConfiguracao(configuracao);
    }
}