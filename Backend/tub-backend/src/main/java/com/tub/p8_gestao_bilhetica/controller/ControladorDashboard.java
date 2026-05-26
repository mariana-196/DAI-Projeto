package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnalitico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorDashboard {

    private final MotorCalculoAnalitico motorCalculoAnalitico;
    private final com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository registoRepository;
    private static final List<IntervaloHorario> INTERVALOS_TUB = List.of(
            new IntervaloHorario("06h20 - 08h00", LocalTime.of(6, 20), LocalTime.of(8, 0)),
            new IntervaloHorario("08h00 - 10h00", LocalTime.of(8, 0), LocalTime.of(10, 0)),
            new IntervaloHorario("10h00 - 12h00", LocalTime.of(10, 0), LocalTime.of(12, 0)),
            new IntervaloHorario("12h00 - 14h00", LocalTime.of(12, 0), LocalTime.of(14, 0)),
            new IntervaloHorario("14h00 - 16h00", LocalTime.of(14, 0), LocalTime.of(16, 0)),
            new IntervaloHorario("16h00 - 18h00", LocalTime.of(16, 0), LocalTime.of(18, 0)),
            new IntervaloHorario("18h00 - 20h00", LocalTime.of(18, 0), LocalTime.of(20, 0)),
            new IntervaloHorario("20h00 - 22h00", LocalTime.of(20, 0), LocalTime.of(22, 0)),
            new IntervaloHorario("22h00 - 00h00", LocalTime.of(22, 0), LocalTime.MIDNIGHT),
            new IntervaloHorario("00h00 - 01h30", LocalTime.MIDNIGHT, LocalTime.of(1, 30))
    );

    public ControladorDashboard(
            MotorCalculoAnalitico motorCalculoAnalitico,
            com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository registoRepository
    ) {
        this.motorCalculoAnalitico = motorCalculoAnalitico;
        this.registoRepository = registoRepository;
    }

    @GetMapping("/dashboard/resultados")
    public ResultadoAnalitico obterResultados(
            @RequestParam(required = false) Long linhaId,
            @RequestParam(required = false) String periodo
    ) {
        ParametrosAnalise parametros = new ParametrosAnalise();
        parametros.setLinhaId(linhaId);
        parametros.setPeriodo(periodo);

        return motorCalculoAnalitico.calcular(parametros);
    }

    @GetMapping("/analise")
    public org.springframework.http.ResponseEntity<?> obterAnalise(@RequestParam(required = false) String linha) {
        List<RegistoBilhetica> todosOsRegistos = registoRepository.findAll();
        if (linha != null && !linha.isEmpty() && !linha.equals("vazia") && !linha.equalsIgnoreCase("ALL")) {
            todosOsRegistos = todosOsRegistos.stream()
                    .filter(r -> r.getLinha() != null && 
                        (String.valueOf(r.getLinha().getId()).equals(linha) || 
                         r.getLinha().getCodigo().equals(linha)))
                    .toList();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("taxaOcupacaoMedia", calcularTaxaOcupacaoMedia(todosOsRegistos));
        map.put("totalPassageiros", todosOsRegistos.stream()
                .mapToInt(r -> r.getValidacoes() != null ? r.getValidacoes() : 0)
                .sum());

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioServico = hoje.atTime(6, 20);
        LocalDateTime fimServico = hoje.plusDays(1).atTime(1, 30);

        List<Integer> procura = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (IntervaloHorario intervalo : INTERVALOS_TUB) {
            LocalDateTime inicio = intervalo.toDateTime(hoje, false);
            LocalDateTime fim = intervalo.toDateTime(hoje, true);
            int totalIntervalo = todosOsRegistos.stream()
                    .filter(r -> r.getDataHora() != null
                            && !r.getDataHora().isBefore(inicioServico)
                            && r.getDataHora().isBefore(fimServico)
                            && !r.getDataHora().isBefore(inicio)
                            && r.getDataHora().isBefore(fim))
                    .mapToInt(r -> r.getValidacoes() != null ? r.getValidacoes() : 0)
                    .sum();
            labels.add(intervalo.label());
            procura.add(totalIntervalo);
        }

        map.put("procura", procura);
        map.put("labels", labels);
        map.put("horarioInicio", "06h20");
        map.put("horarioFim", "01h30");

        return org.springframework.http.ResponseEntity.ok(map);
    }

    private double calcularTaxaOcupacaoMedia(List<RegistoBilhetica> registos) {
        double somaTaxas = 0.0;
        int countComCapacidade = 0;

        for (RegistoBilhetica registo : registos) {
            if (registo.getValidacoes() == null) {
                continue;
            }
            int capacidade = 80;
            if (registo.getViatura() != null
                    && registo.getViatura().getCapacidadeMaxima() != null
                    && registo.getViatura().getCapacidadeMaxima() > 0) {
                capacidade = registo.getViatura().getCapacidadeMaxima();
            }
            somaTaxas += ((double) registo.getValidacoes() / capacidade) * 100;
            countComCapacidade++;
        }

        return countComCapacidade > 0 ? somaTaxas / countComCapacidade : 0.0;
    }

    private record IntervaloHorario(String label, LocalTime inicio, LocalTime fim) {
        private LocalDateTime toDateTime(LocalDate dataBase, boolean fimIntervalo) {
            LocalDate data = dataBase;
            if (inicio.equals(LocalTime.MIDNIGHT) || inicio.isBefore(LocalTime.of(6, 20))) {
                data = data.plusDays(1);
            }
            if (fimIntervalo && fim.equals(LocalTime.MIDNIGHT)) {
                return dataBase.plusDays(1).atStartOfDay();
            }
            if (fimIntervalo && fim.isBefore(LocalTime.of(6, 20))) {
                return dataBase.plusDays(1).atTime(fim);
            }
            return data.atTime(fimIntervalo ? fim : inicio);
        }
    }
}
