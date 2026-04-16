package com.tub.controller;

import com.tub.model.RegraNotificacao;
import com.tub.service.NotificationPolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria/notificacoes")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class NotificationPolicyController {

    private final NotificationPolicyService notificationPolicyService;

    public NotificationPolicyController(NotificationPolicyService notificationPolicyService) {
        this.notificationPolicyService = notificationPolicyService;
    }

    @GetMapping
    public List<RegraNotificacao> listar() {
        return notificationPolicyService.listarRegras();
    }

    @PostMapping
    public RegraNotificacao criar(@RequestBody RegraNotificacao regra) {
        return notificationPolicyService.criarRegra(regra);
    }

    @PutMapping("/{id}")
    public RegraNotificacao atualizar(@PathVariable Long id, @RequestBody RegraNotificacao regra) {
        return notificationPolicyService.atualizarRegra(id, regra);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        notificationPolicyService.apagarRegra(id);
    }
}