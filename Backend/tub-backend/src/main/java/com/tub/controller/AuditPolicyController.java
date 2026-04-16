package com.tub.controller;

import com.tub.model.PoliticasAuditoria;
import com.tub.service.AuditPolicyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria/politicas")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuditPolicyController {

    private final AuditPolicyService auditPolicyService;

    public AuditPolicyController(AuditPolicyService auditPolicyService) {
        this.auditPolicyService = auditPolicyService;
    }

    @GetMapping
    public PoliticasAuditoria obterPolitica() {
        return auditPolicyService.obterPoliticaAtual();
    }

    @PutMapping
    public PoliticasAuditoria atualizarPolitica(@RequestBody PoliticasAuditoria politica) {
        return auditPolicyService.atualizarPolitica(politica);
    }
}