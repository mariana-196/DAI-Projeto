package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bilhetica/mapas")
@CrossOrigin(origins = "*")
public class ControladorConfiguracaoMapas {

    private final Map<String, Object> configuracaoAtual = new LinkedHashMap<>();

    public ControladorConfiguracaoMapas() {
        configuracaoAtual.put("tipoVisualizacao", "calor");
        configuracaoAtual.put("camadaBase", "OpenStreetMap");
        configuracaoAtual.put("blocoHorario", "manha");
        configuracaoAtual.put("mostrarLegenda", true);

        configuracaoAtual.put("zoomDefault", 14);
        configuracaoAtual.put("centroLat", 41.5515);
        configuracaoAtual.put("centroLng", -8.4210);
        configuracaoAtual.put("estilo", "outdoor");
        configuracaoAtual.put("mostrarHotspots", true);
    }

    @GetMapping("/configuracao")
    public Map<String, Object> obterConfiguracaoAtual() {
        return configuracaoAtual;
    }

    @PostMapping("/configuracao")
    public Map<String, Object> atualizarConfiguracao(@RequestBody Map<String, Object> novaConfiguracao) {
        configuracaoAtual.putAll(novaConfiguracao);
        return configuracaoAtual;
    }

    @GetMapping("/atual")
    public Map<String, Object> obterConfiguracaoLegada() {
        return configuracaoAtual;
    }

    @PostMapping("/atualizar")
    public Map<String, String> atualizarConfiguracaoLegada(@RequestBody Map<String, Object> novaConfig) {
        configuracaoAtual.putAll(novaConfig);

        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("status", "Sucesso");
        resposta.put("mensagem", "Configurações de visualização de Braga atualizadas.");
        return resposta;
    }
}