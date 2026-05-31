package com.tub.p7_relatorios.controller;

import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p12_kpis_operacionais.model.RegistoPontualidade;
import com.tub.p12_kpis_operacionais.repository.RegistoPontualidadeRepository;
import com.tub.p5_lotacao.model.HistoricoLotacao;
import com.tub.p5_lotacao.repository.HistoricoLotacaoRepository;
import com.tub.p7_relatorios.dto.DadosRelatorio;
import com.tub.p7_relatorios.service.MotorGrafico;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relatorios/operacional")
@CrossOrigin(origins = "*")
public class ControloRelatorioOperacional {

    private final MotorGrafico motorGrafico;
    private final RegistoBilheticaRepository bilheticaRepo;
    private final HistoricoLotacaoRepository lotacaoRepo;
    private final RegistoPontualidadeRepository pontualidadeRepo;

    public ControloRelatorioOperacional(
            MotorGrafico motorGrafico,
            RegistoBilheticaRepository bilheticaRepo,
            HistoricoLotacaoRepository lotacaoRepo,
            RegistoPontualidadeRepository pontualidadeRepo) {
        this.motorGrafico = motorGrafico;
        this.bilheticaRepo = bilheticaRepo;
        this.lotacaoRepo = lotacaoRepo;
        this.pontualidadeRepo = pontualidadeRepo;
    }

    @PostMapping
    public ResponseEntity<DadosRelatorio> gerarRelatorioOperacional(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String linha,
            @RequestParam(required = false) String veiculo,
            @RequestParam(required = false) String paragem
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (dataInicio != null && !dataInicio.isEmpty()) {
                start = LocalDate.parse(dataInicio, dtf).atStartOfDay();
            } else {
                start = LocalDate.now().atStartOfDay();
            }

            if (dataFim != null && !dataFim.isEmpty()) {
                end = LocalDate.parse(dataFim, dtf).atTime(23, 59, 59);
            } else {
                end = LocalDate.now().atTime(23, 59, 59);
            }
        } catch (Exception e) {
            start = LocalDate.now().atStartOfDay();
            end = LocalDate.now().atTime(23, 59, 59);
        }

        final LocalDateTime finalStart = start;
        final LocalDateTime finalEnd = end;

        List<RegistoBilhetica> registos = bilheticaRepo.findAll().stream()
                .filter(r -> r.getDataHora() != null && !r.getDataHora().isBefore(finalStart) && !r.getDataHora().isAfter(finalEnd))
                .collect(Collectors.toList());

        // Apply filters
        if (linha != null && !linha.isEmpty() && !linha.equals("vazia")) {
            registos = registos.stream().filter(r -> r.getLinha() != null && r.getLinha().getCodigo().equals(linha)).collect(Collectors.toList());
        }
        if (veiculo != null && !veiculo.isEmpty()) {
            registos = registos.stream().filter(r -> r.getViatura() != null && String.valueOf(r.getViatura().getCodigo()).equals(veiculo)).collect(Collectors.toList());
        }

        // Group by Linha and Viatura
        Map<String, List<RegistoBilhetica>> agrupados = registos.stream().collect(
                Collectors.groupingBy(r -> {
                    String l = r.getLinha() != null ? r.getLinha().getCodigo() : "N/A";
                    String v = r.getViatura() != null ? String.valueOf(r.getViatura().getCodigo()) : "N/A";
                    return l + "|" + v;
                })
        );

        List<Map<String, Object>> dados = new ArrayList<>();
        int totalPassageirosGeral = 0;
        int sumLotacaoMediaGeral = 0;
        int countGrupos = 0;

        for (Map.Entry<String, List<RegistoBilhetica>> entry : agrupados.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            String codLinha = parts[0];
            String codViatura = parts[1];

            List<RegistoBilhetica> lista = entry.getValue();
            int passageiros = lista.stream().mapToInt(r -> r.getValidacoes() != null ? r.getValidacoes() : 0).sum();
            totalPassageirosGeral += passageiros;

            // Paragem principal = primeira do grupo ou destino
            String principal = lista.isEmpty() ? "N/A" : lista.get(0).getParagemDestino();
            if (principal == null || principal.isEmpty()) principal = lista.get(0).getParagemOrigem();

            // Lotacao (average for this viatura)
            long avgLotacao = 0;
            if (!codViatura.equals("N/A")) {
                List<HistoricoLotacao> hl = lotacaoRepo.findAll().stream()
                        .filter(h -> h.getTimestamp() != null && !h.getTimestamp().isBefore(finalStart) && !h.getTimestamp().isAfter(finalEnd))
                        .filter(h -> h.getViatura() != null && String.valueOf(h.getViatura().getCodigo()).equals(codViatura))
                        .collect(Collectors.toList());
                if (!hl.isEmpty()) {
                    double pctSum = 0;
                    for (HistoricoLotacao h : hl) {
                        int cap = h.getViatura().getCapacidadeMaxima();
                        if (cap > 0) {
                            pctSum += ((double) h.getPassageirosResultantes() / cap) * 100;
                        }
                    }
                    avgLotacao = Math.round(pctSum / hl.size());
                }
            }
            sumLotacaoMediaGeral += avgLotacao;

            // Pontualidade
            int atrasoMinutos = 0;
            if (!codLinha.equals("N/A")) {
                List<RegistoPontualidade> rp = pontualidadeRepo.findAll().stream()
                        .filter(p -> p.getDataHora() != null && !p.getDataHora().isBefore(finalStart) && !p.getDataHora().isAfter(finalEnd))
                        .filter(p -> p.getLinha().equals(codLinha))
                        .filter(p -> p.getViatura().equals("BUS-" + codViatura))
                        .collect(Collectors.toList());
                if (!rp.isEmpty()) {
                    double avgPontual = rp.stream().mapToInt(RegistoPontualidade::getPercentagemPontualidade).average().orElse(100);
                    // se 100% = 0 min, se 0% = 60 min
                    atrasoMinutos = (int) Math.round(((100 - avgPontual) / 100.0) * 60);
                }
            }

            Map<String, Object> reg = new LinkedHashMap<>();
            reg.put("linha", codLinha);
            reg.put("veiculo", "BUS-" + codViatura);
            reg.put("paragem", principal);
            reg.put("lotacaoMedia", avgLotacao + "%");
            reg.put("passageirosTransportados", passageiros);
            reg.put("atrasoMedio", atrasoMinutos + " min");
            dados.add(reg);
            countGrupos++;
        }

        String mediaGlobal = (countGrupos > 0 ? (sumLotacaoMediaGeral / countGrupos) : 0) + "%";
        Map<String, Object> grafico = motorGrafico.gerarResumoGrafico(totalPassageirosGeral, mediaGlobal);
        dados.add(grafico);

        DadosRelatorio relatorio = new DadosRelatorio(
                "Relatório Operacional",
                "OPERACIONAL",
                LocalDateTime.now(),
                "Operador TUB",
                dados
        );

        return ResponseEntity.ok(relatorio);
    }
}