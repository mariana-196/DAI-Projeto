package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria/politicas")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ControloPoliticasSistema {

    private final com.tub.p6_auditoria.service.ControloPoliticasSistema auditPolicyService;

    public ControloPoliticasSistema(
            com.tub.p6_auditoria.service.ControloPoliticasSistema auditPolicyService) {
        this.auditPolicyService = auditPolicyService;
    }

    @GetMapping
    public EntidadeConfiguracoesAuditoria obterPolitica() {
        return auditPolicyService.obterPoliticaAtual();
    }

    @PutMapping
    public EntidadeConfiguracoesAuditoria atualizarPolitica(@RequestBody EntidadeConfiguracoesAuditoria politica) {
        return auditPolicyService.atualizarPolitica(politica);
    }
}