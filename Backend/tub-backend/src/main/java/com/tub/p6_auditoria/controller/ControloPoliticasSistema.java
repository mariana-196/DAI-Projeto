package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria;

import org.springframework.web.bind.annotation.*;

import com.tub.p1_autenticacao.annotation.RequerCargo;

@RestController("controladorPoliticasSistema")
@RequestMapping("/api/auditoria/politicas")
@CrossOrigin(origins = "*")
@RequerCargo("ADMINISTRADOR")
public class ControloPoliticasSistema {

    private final com.tub.p6_auditoria.service.ControloPoliticasSistema auditPolicyService;
    private final com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService;

    @org.springframework.beans.factory.annotation.Autowired
    private jakarta.servlet.http.HttpServletRequest request;

    public ControloPoliticasSistema(
            com.tub.p6_auditoria.service.ControloPoliticasSistema auditPolicyService,
            com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService) {
        this.auditPolicyService = auditPolicyService;
        this.auditService = auditService;
    }

    @GetMapping
    public EntidadeConfiguracoesAuditoria obterPolitica() {
        return auditPolicyService.obterPoliticaAtual();
    }

    @PutMapping
    public EntidadeConfiguracoesAuditoria atualizarPolitica(@RequestBody EntidadeConfiguracoesAuditoria politica) {
        EntidadeConfiguracoesAuditoria res = auditPolicyService.atualizarPolitica(politica);
        try {
            String email = (String) request.getAttribute("utilizador_email");
            String ip = request.getRemoteAddr();
            auditService.registar(
                    email != null ? email : "Sistema",
                    "ALTERAR_CONFIGURACAO",
                    "Auditoria",
                    ip,
                    "AVISO",
                    "Configurações de auditoria alteradas: retenção=" + politica.getDiasRetencao() + " dias, nível=" + politica.getNivelMinimo()
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }
        return res;
    }
}