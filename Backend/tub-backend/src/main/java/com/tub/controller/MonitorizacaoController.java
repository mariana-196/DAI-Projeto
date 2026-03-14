package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitorizacao")
@CrossOrigin(origins = "*")
public class MonitorizacaoController {

    // O Java guarda o estado do Autocarro #405 na memória
    private int passageirosAtual = 10;
    private boolean sinalAtivo = true;

    // Endpoint para o browser saber como está o autocarro agora
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("passageiros", passageirosAtual);
        status.put("sinal", sinalAtivo);
        status.put("capacidade", 50);
        return status;
    }

    // Endpoint para o Simulador enviar as alterações para o Java
    @PostMapping("/atualizar")
    public Map<String, String> atualizar(@RequestBody Map<String, Object> dados) {
        if (dados.containsKey("passageiros")) {
            this.passageirosAtual = (int) dados.get("passageiros");
        }
        if (dados.containsKey("sinal")) {
            this.sinalAtivo = (boolean) dados.get("sinal");
        }
        
        Map<String, String> res = new HashMap<>();
        res.put("resultado", "Sucesso: Dados sincronizados com o Backend");
        return res;
    }
}