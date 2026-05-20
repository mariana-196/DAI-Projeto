package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.tub.p8_gestao_bilhetica.model.DatasetGeoJSON;
import com.tub.p8_gestao_bilhetica.service.MotorInferenciaEspacial;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Importante para o Frontend conseguir ler os dados
public class ControladorRenderizacaoGIS {

    private final MotorInferenciaEspacial motorEspacial;
    public ControladorRenderizacaoGIS(MotorInferenciaEspacial motorEspacial) {
        this.motorEspacial = motorEspacial;
    }

    @GetMapping("/gis/render/dados-mapa")
    public DatasetGeoJSON obterDadosParaMapa() {
        // Chama o serviço que criámos para Braga
        return motorEspacial.gerarDadosEspaciais();
    }

    @GetMapping("/bilhetica/mapa")
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<Map<String, Object>>> obterPontosMapa() {
        DatasetGeoJSON geojson = motorEspacial.gerarDadosEspaciais();
        List<Map<String, Object>> pontos = new ArrayList<>();

        for (Map<String, Object> feature : geojson.getFeatures()) {
            Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
            double[] coords = (double[]) geometry.get("coordinates"); // [lng, lat]
            Map<String, Object> properties = (Map<String, Object>) feature.get("properties");

            String nome = (String) properties.get("nome");
            int totalValidacoes = (int) properties.get("totalValidacoes");
            boolean hotspot = (boolean) properties.get("hotspot");

            Map<String, Object> ponto = new HashMap<>();
            ponto.put("nome", nome);
            ponto.put("lat", coords[1]); // lat
            ponto.put("lng", coords[0]); // lng
            ponto.put("cor", hotspot ? "red" : "blue");
            ponto.put("raio", Math.min(500, 100 + totalValidacoes * 5));

            pontos.add(ponto);
        }

        return ResponseEntity.ok(pontos);
    }
}