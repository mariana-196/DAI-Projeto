package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.RegraNotificacao;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("controladorNotificacoesEventos")
@RequestMapping("/api/auditoria/notificacoes")
@CrossOrigin(origins = "*")
public class ControloNotificacoesEventos {

    private final com.tub.p6_auditoria.service.ControloNotificacoesEventos notificationPolicyService;

    public ControloNotificacoesEventos(
            com.tub.p6_auditoria.service.ControloNotificacoesEventos notificationPolicyService) {
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