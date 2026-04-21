package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.tub.p8_gestao_bilhetica.model.LoteBilhetica;
import com.tub.p8_gestao_bilhetica.model.MetricaIngestao;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.LoteBilheticaRepository;
import com.tub.p8_gestao_bilhetica.repository.MetricaIngestaoRepository;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

@Service
public class BilheticaStorageService {

    private final LoteBilheticaRepository loteRepository;
    private final RegistoBilheticaRepository registoRepository;
    private final MetricaIngestaoRepository metricaRepository;

    public BilheticaStorageService(
            LoteBilheticaRepository loteRepository,
            RegistoBilheticaRepository registoRepository,
            MetricaIngestaoRepository metricaRepository
    ) {
        this.loteRepository = loteRepository;
        this.registoRepository = registoRepository;
        this.metricaRepository = metricaRepository;
    }

    public LoteBilhetica guardarLote(LoteBilhetica lote) {
        return loteRepository.save(lote);
    }

    public List<RegistoBilhetica> guardarRegistos(List<RegistoBilhetica> registos) {
        return registoRepository.saveAll(registos);
    }

    public MetricaIngestao guardarMetrica(MetricaIngestao metrica) {
        return metricaRepository.save(metrica);
    }
}       