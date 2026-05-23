package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.model.MetricaIngestao;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.model.EstadoSincronizacao;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.repository.MetricaIngestaoRepository;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;
import com.tub.p9_monitorizacao_iot.repository.LotacaoViaturaRepository;
import com.tub.p11_gestao_alertas.model.AlertaLotacao;
import com.tub.p11_gestao_alertas.repository.AlertaLotacaoRepository;
import com.tub.p8_gestao_bilhetica.model.ConfiguracaoIntegracao;
import com.tub.p8_gestao_bilhetica.repository.ConfiguracaoIntegracaoRepository;

@Service
public class ProcesadorArmazenamento {

    private final LoteDadosBilheticaRepository loteRepository;
    private final RegistoBilheticaRepository registoRepository;
    private final MetricaIngestaoRepository metricaRepository;
    private final LinhaRepository linhaRepository;
    private final ViaturasRepository viaturasRepository;
    private final LotacaoViaturaRepository lotacaoViaturaRepository;
    private final AlertaLotacaoRepository alertaLotacaoRepository;
    private final ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository;

    public ProcesadorArmazenamento(
            LoteDadosBilheticaRepository loteRepository,
            RegistoBilheticaRepository registoRepository,
            MetricaIngestaoRepository metricaRepository,
            LinhaRepository linhaRepository,
            ViaturasRepository viaturasRepository,
            LotacaoViaturaRepository lotacaoViaturaRepository,
            AlertaLotacaoRepository alertaLotacaoRepository,
            ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository
    ) {
        this.loteRepository = loteRepository;
        this.registoRepository = registoRepository;
        this.metricaRepository = metricaRepository;
        this.linhaRepository = linhaRepository;
        this.viaturasRepository = viaturasRepository;
        this.lotacaoViaturaRepository = lotacaoViaturaRepository;
        this.alertaLotacaoRepository = alertaLotacaoRepository;
        this.configuracaoIntegracaoRepository = configuracaoIntegracaoRepository;
    }

    public LoteDadosBilhetica guardarLote(LoteDadosBilhetica lote) {
        return loteRepository.save(lote);
    }

    public List<RegistoBilhetica> guardarRegistos(List<RegistoBilhetica> registos) {
        return registoRepository.saveAll(registos);
    }

    public MetricaIngestao guardarMetrica(MetricaIngestao metrica) {
        return metricaRepository.save(metrica);
    }

    public synchronized LoteDadosBilhetica processarEGuardarSincronizacao(List<Validation> validations) {
        if (validations == null || validations.isEmpty()) {
            return null;
        }

        // 1. Create and save lot in RECEBIDO state
        LoteDadosBilhetica lote = new LoteDadosBilhetica();
        lote.setCodigoLote("LOTE_" + System.currentTimeMillis());
        lote.setOrigem("INTEGRACAO_API");
        lote.setEstado(EstadoSincronizacao.RECEBIDO);
        lote.setDataImportacao(LocalDateTime.now());
        lote = loteRepository.save(lote);

        // 2. Fetch lines and vehicles from DB for matching
        List<Linha> linhas = linhaRepository.findAll();
        List<Viatura> viaturas = viaturasRepository.findAll();

        List<RegistoBilhetica> registosASalvar = new ArrayList<>();

        for (Validation val : validations) {
            // Find Linha by code
            Linha linha = linhas.stream()
                    .filter(l -> l.getCodigo().equals(val.getLineId()))
                    .findFirst()
                    .orElse(null);

            if (linha == null) {
                // Skip invalid lines to keep DB data clean and referenced correctly
                continue;
            }

            // Find Viatura by code
            Viatura viatura = null;
            if (val.getVehicleId() != null) {
                try {
                    int vehicleCode = Integer.parseInt(val.getVehicleId());
                    viatura = viaturas.stream()
                            .filter(v -> v.getCodigo().equals(vehicleCode))
                            .findFirst()
                            .orElse(null);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            RegistoBilhetica registo = new RegistoBilhetica();
            registo.setLote(lote);
            registo.setLinha(linha);
            registo.setViatura(viatura);
            registo.setDataHora(val.getTimestamp() != null ? val.getTimestamp() : LocalDateTime.now());
            registo.setParagemOrigem(val.getStopId());
            registo.setTipoTitulo(val.getTicketType() != null ? val.getTicketType() : "Bilhete Normal");
            registo.setValidacoes(1); // 1 validation per transaction

            // Infer Zone
            String stop = val.getStopId() != null ? val.getStopId().toLowerCase() : "";
            if (stop.contains("gualtar") || stop.contains("uminho")) {
                registo.setZona("Gualtar");
            } else if (stop.contains("hospital")) {
                registo.setZona("Hospital");
            } else if (stop.contains("bom jesus")) {
                registo.setZona("Bom Jesus");
            } else if (stop.contains("lamaçães") || stop.contains("lamacaes")) {
                registo.setZona("Lamaçães");
            } else {
                registo.setZona("Centro");
            }

            registosASalvar.add(registo);
        }

        // Save registrations
        if (!registosASalvar.isEmpty()) {
            registoRepository.saveAll(registosASalvar);
            lote.setEstado(EstadoSincronizacao.PROCESSADO);
        } else {
            lote.setEstado(EstadoSincronizacao.VALIDADO);
        }

        // Save lot update
        lote = loteRepository.save(lote);

        // Also save integration metrics for auditing
        try {
            MetricaIngestao metrica = new MetricaIngestao();
            metrica.setLote(lote);
            metrica.setRegistosRecebidos(validations.size());
            metrica.setRegistosValidos(registosASalvar.size());
            metrica.setRegistosInvalidos(validations.size() - registosASalvar.size());
            metrica.setEstado(lote.getEstado().name());
            metricaRepository.save(metrica);
        } catch (Exception e) {
            // Ignore metric save errors
        }

        // Trigger dynamic passenger occupancy simulation for the fleet
        try {
            simularSensoresLotacao();
        } catch (Exception e) {
            System.err.println("Erro ao simular sensores de lotação: " + e.getMessage());
        }

        return lote;
    }

    public synchronized void simularSensoresLotacao() {
        ConfiguracaoIntegracao config = configuracaoIntegracaoRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        int maxDelta = (config != null && config.getSimulacaoMaxEntradasSaidas() != null) 
                ? config.getSimulacaoMaxEntradasSaidas() : 10;
        int maxPct = (config != null && config.getSimulacaoMaxOcupacaoPercentual() != null) 
                ? config.getSimulacaoMaxOcupacaoPercentual() : 90;

        List<EstadoOcupacaoViatura> estados = lotacaoViaturaRepository.findAll();
        java.util.Random random = new java.util.Random();

        for (EstadoOcupacaoViatura estado : estados) {
            Viatura v = estado.getViatura();
            if (v == null) continue;

            int capMax = v.getCapacidadeMaxima() != null ? v.getCapacidadeMaxima() : 80;
            int maxOcupacaoAbsoluta = (int) Math.floor(capMax * (maxPct / 100.0));

            // Generate random entries/exits within [-maxDelta, maxDelta]
            int delta = random.nextInt(maxDelta * 2 + 1) - maxDelta;
            int novosPassageiros = (estado.getPassageirosAtuais() != null ? estado.getPassageirosAtuais() : 0) + delta;

            if (novosPassageiros < 0) {
                novosPassageiros = 0;
            }
            if (novosPassageiros > maxOcupacaoAbsoluta) {
                novosPassageiros = maxOcupacaoAbsoluta;
            }

            double taxa = ((double) novosPassageiros / capMax) * 100;

            estado.setPassageirosAtuais(novosPassageiros);
            estado.setTaxaOcupacao(taxa);
            estado.setUltimaAtualizacao(LocalDateTime.now());
            lotacaoViaturaRepository.save(estado);

            // Handle alert generation on exceeding pre-defined limits (>= 70%)
            if (taxa >= 70.0) {
                boolean alertaExiste = alertaLotacaoRepository.findAll().stream()
                        .anyMatch(a -> a.getViatura() != null && 
                                       a.getViatura().getId().equals(v.getId()) && 
                                       a.getEstado() != null && 
                                       !a.getEstado().equalsIgnoreCase("RESOLVIDO"));

                if (!alertaExiste) {
                    AlertaLotacao novoAlerta = new AlertaLotacao(
                            v,
                            estado.getLinha(),
                            "CRITICO",
                            "PENDENTE",
                            "Lotação Crítica - Ocupação atingiu " + String.format("%.1f", taxa) + "% na " + estado.getLinha()
                    );
                    alertaLotacaoRepository.save(novoAlerta);
                }
            }
        }
    }
}