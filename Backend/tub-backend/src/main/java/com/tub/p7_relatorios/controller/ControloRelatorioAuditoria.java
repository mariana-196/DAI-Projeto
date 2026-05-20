package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.DadosRelatorio;
import com.tub.p7_relatorios.service.ControloAnonimizacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios/auditoria")
@CrossOrigin(origins = "*")
public class ControloRelatorioAuditoria {

    private final ControloAnonimizacao controloAnonimizacao;

    public ControloRelatorioAuditoria(ControloAnonimizacao controloAnonimizacao) {
        this.controloAnonimizacao = controloAnonimizacao;
    }

    @PostMapping
    public ResponseEntity<DadosRelatorio> gerarRelatorioAuditoria(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String utilizador,
            @RequestParam(required = false) String severidade,
            @RequestParam(required = false) String evento
    ) {
        List<Map<String, Object>> dados = new ArrayList<>();

        Map<String, Object> registo1 = new LinkedHashMap<>();
        registo1.put("data", "2026-04-20 10:30");
        registo1.put("utilizador", controloAnonimizacao.anonimizarEmail("admin@tub.pt"));
        registo1.put("evento", "LOGIN");
        registo1.put("severidade", "INFO");
        registo1.put("resultado", "Sucesso");

        Map<String, Object> registo2 = new LinkedHashMap<>();
        registo2.put("data", "2026-04-20 11:05");
        registo2.put("utilizador", controloAnonimizacao.anonimizarEmail("operador@tub.pt"));
        registo2.put("evento", "TENTATIVA_FALHADA");
        registo2.put("severidade", "AVISO");
        registo2.put("resultado", "Falha");

        dados.add(registo1);
        dados.add(registo2);

        DadosRelatorio relatorio = new DadosRelatorio(
                "Relatório de Auditoria",
                "AUDITORIA",
                LocalDateTime.now(),
                "Administrador",
                dados
        );

        return ResponseEntity.ok(relatorio);
    }
}