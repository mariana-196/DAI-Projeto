package com.tub.service;

import org.springframework.stereotype.Service;
import java.util.Optional;

import com.tub.model.MetricaIngestao;
import com.tub.repository.MetricaIngestaoRepository;

@Service
public class BilheticaImportStatusService {

    private final MetricaIngestaoRepository metricaRepository;

    public BilheticaImportStatusService(MetricaIngestaoRepository metricaRepository) {
        this.metricaRepository = metricaRepository;
    }

    public String obterEstadoImportacao() {
        Optional<MetricaIngestao> ultimaMetrica = metricaRepository.findAll()
                .stream()
                .reduce((first, second) -> second);

        if (ultimaMetrica.isEmpty()) {
            return "Sem dados de importação";
        }

        MetricaIngestao metrica = ultimaMetrica.get();

        if (metrica.getEstado() == null) {
            return "Estado desconhecido";
        }

        return metrica.getEstado();
    }

    public boolean importacaoComSucesso() {
        Optional<MetricaIngestao> ultimaMetrica = metricaRepository.findAll()
                .stream()
                .reduce((first, second) -> second);

        if (ultimaMetrica.isEmpty() || ultimaMetrica.get().getEstado() == null) {
            return false;
        }

        return "SUCESSO".equalsIgnoreCase(ultimaMetrica.get().getEstado())
                || "PROCESSADO".equalsIgnoreCase(ultimaMetrica.get().getEstado());
    }
}