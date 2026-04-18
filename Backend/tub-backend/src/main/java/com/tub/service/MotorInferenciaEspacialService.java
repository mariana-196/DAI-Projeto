package com.tub.service;

import org.springframework.stereotype.Service;
import com.tub.model.DatasetGeoJSON;
import com.tub.model.RegistoBilhetica;
import com.tub.repository.RegistoBilheticaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MotorInferenciaEspacialService { // Nome corrigido aqui

    private final RegistoBilheticaRepository registoRepository;

    // Construtor corrigido para ter exatamente o mesmo nome da classe
    public MotorInferenciaEspacialService(RegistoBilheticaRepository registoRepository) { 
        this.registoRepository = registoRepository;
    }

    public DatasetGeoJSON gerarDadosEspaciais() {
        DatasetGeoJSON geojson = new DatasetGeoJSON();
        List<RegistoBilhetica> registos = registoRepository.findAll();
        
        // 1. Agrupar total de passageiros por paragem
        Map<String, Integer> passageirosPorParagem = new HashMap<>();
        for (RegistoBilhetica r : registos) {
            String paragem = r.getParagemOrigem();
            if (paragem != null) {
                int validacoes = (r.getValidacoes() != null) ? r.getValidacoes() : 1;
                passageirosPorParagem.put(paragem, passageirosPorParagem.getOrDefault(paragem, 0) + validacoes);
            }
        }

        // 2. Transformar cada paragem num ponto no mapa
        for (Map.Entry<String, Integer> entry : passageirosPorParagem.entrySet()) {
            String nomeParagem = entry.getKey();
            int totalPassageiros = entry.getValue();

            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "Feature");
            
            Map<String, Object> geometry = new HashMap<>();
            geometry.put("type", "Point");
            geometry.put("coordinates", inferirCoordenadas(nomeParagem));
            feature.put("geometry", geometry);
            
            Map<String, Object> properties = new HashMap<>();
            properties.put("nome", nomeParagem);
            properties.put("totalValidacoes", totalPassageiros);
            properties.put("hotspot", totalPassageiros > 50); 
            feature.put("properties", properties);

            geojson.getFeatures().add(feature);
        }

        return geojson;
    }

    // --- MÉTODO AUXILIAR PARA BRAGA ---
    private double[] inferirCoordenadas(String paragem) {
        paragem = paragem.toLowerCase();
        
        if (paragem.contains("gualtar") || paragem.contains("uminho")) return new double[]{-8.3970, 41.5610};
        if (paragem.contains("central") || paragem.contains("avenida")) return new double[]{-8.4210, 41.5515};
        if (paragem.contains("hospital")) return new double[]{-8.3990, 41.5670};
        if (paragem.contains("estação") || paragem.contains("cp")) return new double[]{-8.4340, 41.5490};
        if (paragem.contains("arcada")) return new double[]{-8.4230, 41.5510};
        if (paragem.contains("bom jesus")) return new double[]{-8.3775, 41.5545};
        
        double lng = -8.4200 + (Math.random() * 0.03 - 0.015);
        double lat = 41.5500 + (Math.random() * 0.03 - 0.015);
        return new double[]{lng, lat};
    }
}
