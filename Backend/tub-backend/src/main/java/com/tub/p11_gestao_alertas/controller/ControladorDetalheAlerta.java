package com.tub.p11_gestao_alertas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas/detalhe")
@CrossOrigin(origins = "*")
public class ControladorDetalheAlerta {

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obterDetalheAlerta(@PathVariable Long id) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("mensagem", "Detalhe do alerta");
        resposta.put("id", id);

        return ResponseEntity.ok(resposta);
    }
}