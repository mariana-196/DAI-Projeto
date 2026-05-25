package com.tub.p6_auditoria.controller;

import com.tub.p6_auditoria.model.RegraNotificacao;
import com.tub.p6_auditoria.repository.RegraNotificacaoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regras-auditoria")
// Esta anotação permite que o teu ecrã HTML consiga falar com o Java sem erros de bloqueio (CORS)
@CrossOrigin(origins = "*") 
public class RegrasAuditoriaController {

    private final RegraNotificacaoRepository regraRepo;

    public RegrasAuditoriaController(RegraNotificacaoRepository regraRepo) {
        this.regraRepo = regraRepo;
    }

    // Pede ao Java para devolver todas as regras (para preencher a tua tabela no ecrã)
    @GetMapping
    public List<RegraNotificacao> listarRegras() {
        return regraRepo.findAll();
    }

    // Recebe os dados do formulário do ecrã e grava na Base de Dados
    @PostMapping
    public RegraNotificacao criarRegra(@RequestBody RegraNotificacao novaRegra) {
        // Garante que a regra fica ativa por defeito quando é criada
        if (novaRegra.getAtiva() == null) {
            novaRegra.setAtiva(true);
        }
        return regraRepo.save(novaRegra);
    }
}
