package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gis/config")
@CrossOrigin(origins = "*")
public class ConfiguracaoMapasController {

    // Mapa em memória para guardar as definições de visualização de Braga
    private Map<String, Object> configAtual = new HashMap<>();

    public ConfiguracaoMapasController() {
        // Configurações padrão para centrar o mapa na Avenida Central de Braga
        configAtual.put("zoomDefault", 14);
        configAtual.put("centroLat", 41.5515);
        configAtual.put("centroLng", -8.4210);
        configAtual.put("estilo", "outdoor"); // Pode ser 'streets', 'dark', 'satellite', etc.
        configAtual.put("mostrarHotspots", true);
    }

    // Endpoint para o Frontend saber onde centrar o mapa ao carregar
    @GetMapping("/atual")
    public Map<String, Object> obterConfiguracao() {
        return configAtual;
    }

    // Endpoint para permitir que o administrador mude o zoom ou centro do mapa via UI
    @PostMapping("/atualizar")
    public Map<String, String> atualizarConfiguracao(@RequestBody Map<String, Object> novaConfig) {
        this.configAtual.putAll(novaConfig);
        
        Map<String, String> resposta = new HashMap<>();
        resposta.put("status", "Sucesso");
        resposta.put("mensagem", "Configurações de visualização de Braga atualizadas!");
        return resposta;
    }
}