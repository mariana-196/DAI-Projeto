package com.tub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Map;

@RestController
@CrossOrigin // Permite que o Frontend aceda ao Backend
public class MockDataController {

    @GetMapping("/api/alertas")
    public Map<String, String> getAlerta() {
        // Simulando o Vertical 3.4 (Contagem de Passageiros)
        return Map.of(
            "titulo", "⚠️ Sobrelotação - Linha 2",
            "mensagem", "Autocarro #405 atingiu 98% da capacidade."
        );
    }
}