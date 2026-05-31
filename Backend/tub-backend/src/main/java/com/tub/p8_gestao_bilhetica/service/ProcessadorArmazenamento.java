package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
import com.tub.p11_gestao_alertas.model.AlertaOperacional;
import com.tub.p11_gestao_alertas.repository.AlertaOperacionalRepository;
import com.tub.p8_gestao_bilhetica.model.ConfiguracaoIntegracao;
import com.tub.p8_gestao_bilhetica.repository.ConfiguracaoIntegracaoRepository;

@Service
public class ProcessadorArmazenamento {

    private static final Map<String, double[]> COORDENADAS_PARAGENS = new HashMap<>();
    private static final String[] PARAGENS_DEMO = {
            "Universidade do Minho", "Estacao CP", "Hospital de Braga",
            "Avenida Central", "Bom Jesus", "Arcada", "Gualtar",
            "Braga Parque", "Lamacaes"
    };
    private static final LocalTime[] HORAS_SERVICO_DEMO = {
            LocalTime.of(6, 35), LocalTime.of(8, 40), LocalTime.of(10, 30),
            LocalTime.of(12, 25), LocalTime.of(14, 35), LocalTime.of(16, 45),
            LocalTime.of(18, 30), LocalTime.of(20, 35), LocalTime.of(22, 40),
            LocalTime.of(0, 45)
    };

    static {
        COORDENADAS_PARAGENS.put("Universidade do Minho", new double[]{41.5612, -8.3978});
        COORDENADAS_PARAGENS.put("Gualtar - Universidade do Minho", new double[]{41.5612, -8.3978});
        COORDENADAS_PARAGENS.put("Estacao CP", new double[]{41.5492, -8.4344});
        COORDENADAS_PARAGENS.put("EstaÃ§Ã£o CP", new double[]{41.5492, -8.4344});
        COORDENADAS_PARAGENS.put("Hospital de Braga", new double[]{41.5683, -8.3995});
        COORDENADAS_PARAGENS.put("Avenida Central", new double[]{41.5518, -8.4229});
        COORDENADAS_PARAGENS.put("Bom Jesus", new double[]{41.5546, -8.3775});
        COORDENADAS_PARAGENS.put("Arcada", new double[]{41.5509, -8.4260});
        COORDENADAS_PARAGENS.put("Gualtar", new double[]{41.5600, -8.3947});
        COORDENADAS_PARAGENS.put("Braga Parque", new double[]{41.5586, -8.4059});
        COORDENADAS_PARAGENS.put("Lamacaes", new double[]{41.5414, -8.3954});
        COORDENADAS_PARAGENS.put("LamaÃ§Ã£es", new double[]{41.5414, -8.3954});
    }

    private final LoteDadosBilheticaRepository loteRepository;
    private final RegistoBilheticaRepository registoRepository;
    private final MetricaIngestaoRepository metricaRepository;
    private final LinhaRepository linhaRepository;
    private final ViaturasRepository viaturasRepository;
    private final LotacaoViaturaRepository lotacaoViaturaRepository;
    private final AlertaOperacionalRepository alertaOperacionalRepository;
    private final ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository;

    public ProcessadorArmazenamento(
            LoteDadosBilheticaRepository loteRepository,
            RegistoBilheticaRepository registoRepository,
            MetricaIngestaoRepository metricaRepository,
            LinhaRepository linhaRepository,
            ViaturasRepository viaturasRepository,
            LotacaoViaturaRepository lotacaoViaturaRepository,
            AlertaOperacionalRepository alertaOperacionalRepository,
            ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository
    ) {
        this.loteRepository = loteRepository;
        this.registoRepository = registoRepository;
        this.metricaRepository = metricaRepository;
        this.linhaRepository = linhaRepository;
        this.viaturasRepository = viaturasRepository;
        this.lotacaoViaturaRepository = lotacaoViaturaRepository;
        this.alertaOperacionalRepository = alertaOperacionalRepository;
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
            String origem = normalizarParagem(val.getStopId());
            String destino = inferirDestino(origem, linha != null ? linha.getDestino() : null, new Random());
            registo.setParagemOrigem(origem);
            registo.setParagemDestino(destino);
            registo.setTipoTitulo(val.getTicketType() != null ? val.getTicketType() : "Bilhete Normal");
            registo.setValidacoes(1); // 1 validation per transaction
            aplicarCoordenadas(registo, origem, destino);

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

    public synchronized LoteDadosBilhetica simularFluxosBilheticaAleatorios(String origemSincronizacao) {
        List<Linha> linhas = linhaRepository.findAll();
        List<Viatura> viaturas = viaturasRepository.findAll().stream()
                .filter(Viatura::isAtiva)
                .toList();

        if (linhas.isEmpty() || viaturas.isEmpty()) {
            return null;
        }

        ConfiguracaoIntegracao config = configuracaoIntegracaoRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        int maxDelta = (config != null && config.getSimulacaoMaxEntradasSaidas() != null)
                ? Math.max(1, config.getSimulacaoMaxEntradasSaidas()) : 10;
        int maxValidacoesPorLeitura = Math.min(maxDelta, 6);

        Random random = new Random();
        int totalAutocarrosConsiderados = Math.min(6, viaturas.size());

        LoteDadosBilhetica lote = new LoteDadosBilhetica();
        lote.setCodigoLote("LOTE_SYNC_" + System.currentTimeMillis());
        lote.setOrigem(origemSincronizacao != null ? origemSincronizacao : "SINCRONIZACAO_PARTILHADA");
        lote.setEstado(EstadoSincronizacao.RECEBIDO);
        lote.setDataImportacao(LocalDateTime.now());
        lote = loteRepository.save(lote);

        List<RegistoBilhetica> registos = new ArrayList<>();
        String[] titulos = {"Passe Estudante", "Bilhete Normal", "Passe Senior", "Passe Turistico"};

        for (int intervalo = 0; intervalo < HORAS_SERVICO_DEMO.length; intervalo++) {
            for (int linhaIndex = 0; linhaIndex < linhas.size(); linhaIndex++) {
                Linha linha = linhas.get(linhaIndex);
                Viatura viatura = viaturas.get((intervalo + linhaIndex) % totalAutocarrosConsiderados);
                
                String[] paragensDaLinha = paragensParaLinha(linha.getCodigo());
                String origem = paragensDaLinha[random.nextInt(paragensDaLinha.length)];
                String destino = inferirDestinoRestrito(origem, linha.getDestino(), paragensDaLinha, random);
                int entradas = 1 + random.nextInt(Math.max(1, Math.min(maxValidacoesPorLeitura, 3)));

                RegistoBilhetica registo = new RegistoBilhetica();
                registo.setLote(lote);
                registo.setLinha(linha);
                registo.setViatura(viatura);
                registo.setDataHora(gerarInstanteServico(intervalo, random));
                registo.setParagemOrigem(origem);
                registo.setParagemDestino(destino);
                registo.setTipoTitulo(titulos[random.nextInt(titulos.length)]);
                registo.setValidacoes(entradas);
                registo.setZona(inferirZona(origem));
                aplicarCoordenadas(registo, origem, destino);
                registos.add(registo);
            }
        }

        registoRepository.saveAll(registos);
        lote.setEstado(EstadoSincronizacao.PROCESSADO);
        lote = loteRepository.save(lote);

        try {
            MetricaIngestao metrica = new MetricaIngestao();
            metrica.setLote(lote);
            metrica.setRegistosRecebidos(totalAutocarrosConsiderados);
            metrica.setRegistosValidos(registos.size());
            metrica.setRegistosInvalidos(0);
            metrica.setEstado(lote.getEstado().name());
            metricaRepository.save(metrica);
        } catch (Exception e) {
            // Ignore metric save errors
        }

        try {
            simularSensoresLotacao();
        } catch (Exception e) {
            System.err.println("Erro ao simular sensores de lotaÃ§Ã£o: " + e.getMessage());
        }

        return lote;
    }

    private LocalDateTime gerarInstanteServico(int indiceIntervalo, Random random) {
        LocalDateTime agora = LocalDateTime.now();
        LocalTime horaBase = HORAS_SERVICO_DEMO[indiceIntervalo % HORAS_SERVICO_DEMO.length]
                .plusMinutes(random.nextInt(35));
        if (horaBase.isBefore(LocalTime.of(1, 31))) {
            return agora.plusDays(1).withHour(horaBase.getHour()).withMinute(horaBase.getMinute()).withSecond(0).withNano(0);
        }
        return agora.withHour(horaBase.getHour()).withMinute(horaBase.getMinute()).withSecond(0).withNano(0);
    }



    public synchronized void simularSensoresLotacao() {
        java.util.List<com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura> estados = lotacaoViaturaRepository.findAll();
        java.util.Random random = new java.util.Random();

        for (com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura estado : estados) {
            if (!estado.isSinalAtivo() || estado.getViatura() == null) continue;

            int capMax = estado.getViatura().getCapacidadeMaxima() != null ? estado.getViatura().getCapacidadeMaxima() : 80;
            int atuais = estado.getPassageirosAtuais() != null ? estado.getPassageirosAtuais() : 0;
            
            // Random variation between -5 and +8 passengers
            int delta = random.nextInt(14) - 5; 
            int novosPassageiros = atuais + delta;

            if (novosPassageiros < 0) novosPassageiros = 0;
            if (novosPassageiros > capMax) novosPassageiros = capMax;

            double taxa = ((double) novosPassageiros / capMax) * 100;
            estado.setPassageirosAtuais(novosPassageiros);
            estado.setTaxaOcupacao(taxa);
            estado.setUltimaAtualizacao(java.time.LocalDateTime.now());
            lotacaoViaturaRepository.save(estado);

            if (taxa >= 90.0) {
                boolean alertaExiste = alertaOperacionalRepository.findAll().stream()
                        .anyMatch(a -> a.getViatura() != null && 
                                       a.getViatura().getId().equals(estado.getViatura().getId()) && 
                                       a.getEstado() != null && 
                                       !a.getEstado().equalsIgnoreCase("RESOLVIDO") &&
                                       "LOTACAO".equals(a.getTema()));

                if (!alertaExiste) {
                    com.tub.p11_gestao_alertas.model.AlertaOperacional novoAlerta = new com.tub.p11_gestao_alertas.model.AlertaOperacional(
                            estado.getViatura(),
                            estado.getLinha(),
                            "Lotação Crítica (IoT)",
                            "LOTACAO",
                            "CRITICO",
                            "PENDENTE",
                            "Ocupação atingiu " + String.format("%.1f", taxa) + "% na " + estado.getLinha() + " (Viatura #" + estado.getViatura().getCodigo() + ")",
                            "IoT Sensores",
                            null
                    );
                    alertaOperacionalRepository.save(novoAlerta);
                }
            }
        }
    }

    private void aplicarCoordenadas(RegistoBilhetica registo, String origem, String destino) {
        double[] coordOrigem = coordenadasParagem(origem);
        double[] coordDestino = coordenadasParagem(destino);
        registo.setLatitude(coordOrigem[0]);
        registo.setLongitude(coordOrigem[1]);
        registo.setLatitudeDestino(coordDestino[0]);
        registo.setLongitudeDestino(coordDestino[1]);
    }

    private double[] coordenadasParagem(String paragem) {
        if (paragem != null && COORDENADAS_PARAGENS.containsKey(paragem)) {
            return COORDENADAS_PARAGENS.get(paragem);
        }
        return new double[]{41.5510, -8.4230};
    }

    private String normalizarParagem(String paragem) {
        if (paragem == null || paragem.isBlank()) {
            return "Avenida Central";
        }
        return paragem.replace("EstaÃ§Ã£o", "Estacao").replace("LamaÃ§Ã£es", "Lamacaes");
    }

    private String inferirDestino(String origem, String destinoLinha, Random random) {
        String destino = normalizarParagem(destinoLinha);
        if (destino.equals(origem) || !COORDENADAS_PARAGENS.containsKey(destino)) {
            do {
                destino = PARAGENS_DEMO[random.nextInt(PARAGENS_DEMO.length)];
            } while (destino.equals(origem));
        }
        return destino;
    }

    private String[] paragensParaLinha(String codigo) {
        if ("2".equals(codigo)) return new String[]{"Hospital de Braga", "Avenida Central", "Estacao CP", "Arcada"};
        if ("7".equals(codigo)) return new String[]{"Bom Jesus", "Arcada", "Avenida Central"};
        if ("24".equals(codigo)) return new String[]{"Gualtar", "Braga Parque", "Avenida Central"};
        if ("15".equals(codigo)) return new String[]{"Lamacaes", "Estacao CP", "Avenida Central"};
        if ("43".equals(codigo)) return new String[]{"Universidade do Minho", "Gualtar", "Estacao CP", "Avenida Central"};
        return PARAGENS_DEMO;
    }

    private String inferirDestinoRestrito(String origem, String destinoLinha, String[] paragensDaLinha, Random random) {
        String destino = normalizarParagem(destinoLinha);
        if (destino.equals(origem) || !COORDENADAS_PARAGENS.containsKey(destino)) {
            do {
                destino = paragensDaLinha[random.nextInt(paragensDaLinha.length)];
            } while (destino.equals(origem) && paragensDaLinha.length > 1);
        }
        return destino;
    }

    private String inferirZona(String paragem) {
        String stop = paragem != null ? paragem.toLowerCase() : "";
        if (stop.contains("gualtar") || stop.contains("uminho")) return "Gualtar";
        if (stop.contains("hospital")) return "Hospital";
        if (stop.contains("bom jesus")) return "Bom Jesus";
        if (stop.contains("lamacaes") || stop.contains("lama")) return "Lamacaes";
        if (stop.contains("parque")) return "Braga Parque";
        return "Centro";
    }
}
