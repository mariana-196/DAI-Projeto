package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;

import com.tub.p8_gestao_bilhetica.model.DatasetGeoJSON;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MotorInferenciaEspacial {

    private final RegistoBilheticaRepository registoRepository;

    public MotorInferenciaEspacial(RegistoBilheticaRepository registoRepository) {
        this.registoRepository = registoRepository;
    }

    public DatasetGeoJSON gerarDadosEspaciais() {
        return gerarDadosEspaciais("heatmap", null, null, null, null, null);
    }

    public DatasetGeoJSON gerarDadosEspaciais(
            String tipoMapa,
            String linha,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer horaInicio,
            Integer horaFim
    ) {
        DatasetGeoJSON geojson = new DatasetGeoJSON();
        List<RegistoBilhetica> registos = filtrarRegistos(linha, dataInicio, dataFim, horaInicio, horaFim);

        if (dataInicio != null && dataFim != null && ChronoUnit.DAYS.between(dataInicio, dataFim) > 366) {
            geojson.getFeatures().add(featureAviso("PROCESSAMENTO_ASSINCRONO",
                    "O processamento deste volume de dados e extenso. A tarefa foi agendada em background."));
            return geojson;
        }

        int totalAmostra = registos.stream()
                .mapToInt(r -> r.getValidacoes() != null ? r.getValidacoes() : 1)
                .sum();

        if (totalAmostra < 10) {
            geojson.getFeatures().add(featureAviso("AMOSTRA_INSUFICIENTE",
                    "Amostra insuficiente para inferencia. Alargue o intervalo temporal ou altere os filtros."));
            return geojson;
        }

        if ("od".equalsIgnoreCase(tipoMapa) || "fluxos".equalsIgnoreCase(tipoMapa)) {
            gerarFluxosOd(geojson, registos);
        } else {
            gerarHeatmap(geojson, registos);
        }

        return geojson;
    }

    private void gerarHeatmap(DatasetGeoJSON geojson, List<RegistoBilhetica> registos) {
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
            properties.put("tipo", "heatmap");
            properties.put("privacidadeProtegida", false);
            feature.put("properties", properties);

            geojson.getFeatures().add(feature);
        }
    }

    private void gerarFluxosOd(DatasetGeoJSON geojson, List<RegistoBilhetica> registos) {
        Map<String, FluxoAggregator> fluxos = new HashMap<>();

        for (RegistoBilhetica r : registos) {
            String origem = r.getParagemOrigem();
            String destino = r.getParagemDestino();
            if (origem == null || destino == null || origem.equals(destino)) {
                continue;
            }

            String key = origem + "->" + destino;
            FluxoAggregator agg = fluxos.computeIfAbsent(key, k -> new FluxoAggregator(
                    origem,
                    destino,
                    r.getLatitude(),
                    r.getLongitude(),
                    r.getLatitudeDestino(),
                    r.getLongitudeDestino()
            ));
            agg.addValidacoes(r.getValidacoes() != null ? r.getValidacoes() : 1);
        }

        for (FluxoAggregator agg : fluxos.values()) {
            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "Feature");

            Map<String, Object> geometry = new HashMap<>();
            geometry.put("type", "LineString");
            double origemLng = agg.longitudeOrigem != null ? agg.longitudeOrigem : -8.4230;
            double origemLat = agg.latitudeOrigem != null ? agg.latitudeOrigem : 41.5510;
            double destinoLng = agg.longitudeDestino != null ? agg.longitudeDestino : -8.4230;
            double destinoLat = agg.latitudeDestino != null ? agg.latitudeDestino : 41.5510;
            geometry.put("coordinates", new double[][]{
                    new double[]{origemLng, origemLat},
                    new double[]{destinoLng, destinoLat}
            });
            feature.put("geometry", geometry);

            Map<String, Object> properties = new HashMap<>();
            properties.put("tipo", "od");
            properties.put("origem", agg.origem);
            properties.put("destino", agg.destino);
            properties.put("volume", agg.totalPassageiros);
            properties.put("privacidadeProtegida", false);
            feature.put("properties", properties);

            geojson.getFeatures().add(feature);
        }
    }

    private List<RegistoBilhetica> filtrarRegistos(
            String linha,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer horaInicio,
            Integer horaFim
    ) {
        List<RegistoBilhetica> registos = new ArrayList<>(registoRepository.findAll());

        return registos.stream()
                .filter(r -> r.getDataHora() != null)
                .filter(r -> linha == null || linha.isBlank() || "ALL".equalsIgnoreCase(linha)
                        || (r.getLinha() != null && linha.equals(r.getLinha().getCodigo())))
                .filter(r -> dataInicio == null || !r.getDataHora().toLocalDate().isBefore(dataInicio))
                .filter(r -> dataFim == null || !r.getDataHora().toLocalDate().isAfter(dataFim))
                .filter(r -> horaInicio == null || r.getDataHora().getHour() >= horaInicio)
                .filter(r -> horaFim == null || r.getDataHora().getHour() <= horaFim)
                .collect(Collectors.toList());
    }

    private Map<String, Object> featureAviso(String codigo, String mensagem) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", new double[]{-8.4230, 41.5510});
        feature.put("geometry", geometry);

        Map<String, Object> properties = new HashMap<>();
        properties.put("tipo", "aviso");
        properties.put("codigo", codigo);
        properties.put("mensagem", mensagem);
        feature.put("properties", properties);

        return feature;
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

    private static class FluxoAggregator {
        String origem;
        String destino;
        Double latitudeOrigem;
        Double longitudeOrigem;
        Double latitudeDestino;
        Double longitudeDestino;
        int totalPassageiros = 0;

        public FluxoAggregator(String origem, String destino, Double latitudeOrigem, Double longitudeOrigem,
                               Double latitudeDestino, Double longitudeDestino) {
            this.origem = origem;
            this.destino = destino;
            this.latitudeOrigem = latitudeOrigem;
            this.longitudeOrigem = longitudeOrigem;
            this.latitudeDestino = latitudeDestino;
            this.longitudeDestino = longitudeDestino;
        }

        public void addValidacoes(int qtd) {
            this.totalPassageiros += qtd;
        }
    }
}
