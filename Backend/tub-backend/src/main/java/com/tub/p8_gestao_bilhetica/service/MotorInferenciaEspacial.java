package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;

import com.tub.p8_gestao_bilhetica.model.DatasetGeoJSON;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MotorInferenciaEspacial {

    private final RegistoBilheticaRepository registoRepository;

    public MotorInferenciaEspacial(RegistoBilheticaRepository registoRepository) {
        this.registoRepository = registoRepository;
    }

    public DatasetGeoJSON gerarDadosEspaciais() {
        DatasetGeoJSON geojson = new DatasetGeoJSON();
        List<RegistoBilhetica> registos = registoRepository.findAll();

        Map<String, RegistoAggregator> dadosPorParagem = new HashMap<>();

        for (RegistoBilhetica r : registos) {
            String paragem = r.getParagemOrigem();
            if (paragem != null) {
                int validacoes = (r.getValidacoes() != null) ? r.getValidacoes() : 1;
                dadosPorParagem.putIfAbsent(paragem, new RegistoAggregator(paragem, r.getLatitude(), r.getLongitude()));
                dadosPorParagem.get(paragem).addValidacoes(validacoes);
            }
        }

        for (RegistoAggregator agg : dadosPorParagem.values()) {
            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "Feature");

            Map<String, Object> geometry = new HashMap<>();
            geometry.put("type", "Point");
            // If latitude or longitude is null, provide a default or skip
            double lng = agg.longitude != null ? agg.longitude : -8.4200;
            double lat = agg.latitude != null ? agg.latitude : 41.5500;
            geometry.put("coordinates", new double[]{lng, lat});
            feature.put("geometry", geometry);

            Map<String, Object> properties = new HashMap<>();
            properties.put("nome", agg.nomeParagem);
            properties.put("totalValidacoes", agg.totalPassageiros);
            properties.put("hotspot", agg.totalPassageiros > 50);
            feature.put("properties", properties);

            geojson.getFeatures().add(feature);
        }

        return geojson;
    }

    private static class RegistoAggregator {
        String nomeParagem;
        Double latitude;
        Double longitude;
        int totalPassageiros = 0;

        public RegistoAggregator(String nomeParagem, Double latitude, Double longitude) {
            this.nomeParagem = nomeParagem;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public void addValidacoes(int qtd) {
            this.totalPassageiros += qtd;
        }
    }
}
