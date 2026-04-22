package com.tub.p11_gestao_alertas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas/gestao")
@CrossOrigin(origins = "*")
public class ControladorGestaoAlerta {

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizarAlerta(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("mensagem", "Alteração do alerta recebida");
        resposta.put("id", id);
        resposta.put("dados", body);

        return ResponseEntity.ok(resposta);
    }
}