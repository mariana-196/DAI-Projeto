package com.tub.p12_kpis_operacionais.controller;

import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p5_lotacao.repository.ViaturasRepository;

import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import com.tub.p11_gestao_alertas.model.AlertaOperacional;
import com.tub.p11_gestao_alertas.repository.AlertaOperacionalRepository;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ControladorDadosDashboard {

    private final ViaturasRepository viaturasRepository;
    private final RegistoBilheticaRepository registoBilheticaRepository;
    private final AlertaOperacionalRepository alertaOperacionalRepository;
    private final com.tub.p12_kpis_operacionais.repository.RegistoPontualidadeRepository registoPontualidadeRepository;

    public ControladorDadosDashboard(
            ViaturasRepository viaturasRepository,
            RegistoBilheticaRepository registoBilheticaRepository,
            AlertaOperacionalRepository alertaOperacionalRepository,
            com.tub.p12_kpis_operacionais.repository.RegistoPontualidadeRepository registoPontualidadeRepository
    ) {
        this.viaturasRepository = viaturasRepository;
        this.registoBilheticaRepository = registoBilheticaRepository;
        this.alertaOperacionalRepository = alertaOperacionalRepository;
        this.registoPontualidadeRepository = registoPontualidadeRepository;
    }

    @GetMapping("/dashboard/kpis")
    public Map<String, Object> obterKpisDashboard() {
        List<Viatura> viaturas = viaturasRepository.findAll();
        List<RegistoBilhetica> registos = registoBilheticaRepository.findAll();
        List<AlertaOperacional> alertas = alertaOperacionalRepository.findAll();

        long totalViaturas = viaturas.size();
        long viaturasAtivas = viaturas.stream()
                .filter(Viatura::isAtiva)
                .count();

        LocalDate hoje = LocalDate.now();

        LocalDateTime inicioDia = hoje.atTime(6, 20);
        LocalDateTime fimDia = hoje.plusDays(1).atTime(1, 30);
        LocalDateTime agora = LocalDateTime.now();

        int totalValidacoes = 0;
        for (RegistoBilhetica registo : registos) {
            if (registo.getValidacoes() != null
                    && registo.getDataHora() != null
                    && !registo.getDataHora().isBefore(inicioDia)
                    && registo.getDataHora().isBefore(fimDia)
                    && registo.getDataHora().isBefore(agora)) {
                totalValidacoes += registo.getValidacoes();
            }
        }

        long activeAlerts = alertas.stream()
                .filter(a -> a.getEstado() == null || !a.getEstado().equalsIgnoreCase("RESOLVIDO"))
                .count();

        List<com.tub.p12_kpis_operacionais.model.RegistoPontualidade> registosPontualidade = registoPontualidadeRepository.findAll();
        long punctuality = 0;
        if (registosPontualidade.isEmpty()) {
            punctuality = 100 - (activeAlerts * 2);
            if (punctuality < 75) punctuality = 75;
            else if (punctuality > 98) punctuality = 98;
        } else {
            double average = registosPontualidade.stream()
                    .mapToInt(com.tub.p12_kpis_operacionais.model.RegistoPontualidade::getPercentagemPontualidade)
                    .average()
                    .orElse(100.0);
            punctuality = Math.round(average);
        }

        long falhasDMS = alertas.stream()
                .filter(a -> a.getTema() != null && a.getTema().toUpperCase().contains("DMS") && 
                             (a.getEstado() == null || !a.getEstado().equalsIgnoreCase("RESOLVIDO")))
                .count();

        long falhasGPS = alertas.stream()
                .filter(a -> a.getTema() != null && a.getTema().toUpperCase().contains("GPS") && 
                             (a.getEstado() == null || !a.getEstado().equalsIgnoreCase("RESOLVIDO")))
                .count();

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("ativas", viaturasAtivas);
        kpis.put("total", totalViaturas);
        kpis.put("pontualidade", punctuality);
        kpis.put("validacoes", totalValidacoes);
        kpis.put("falhasDMS", falhasDMS);
        kpis.put("falhasGPS", falhasGPS);

        return kpis;
    }

    @GetMapping({"/alertas", "/alerts"})
    public List<Map<String, Object>> obterAlertasDashboard() {
        List<AlertaOperacional> alertas = alertaOperacionalRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (AlertaOperacional alerta : alertas) {
            Map<String, Object> item = new HashMap<>();

            item.put("id", alerta.getId());
            item.put("titulo", alerta.getTitulo());
            item.put("tema", alerta.getTema());
            item.put("estado", formatarEstado(alerta.getEstado()));
            item.put("descricao", alerta.getDescricao());
            item.put("prioridade", calcularPrioridade(alerta.getSeveridade()));
            item.put("origem", alerta.getOrigem());
            item.put("linha", alerta.getLinha());
            item.put("timestamp", alerta.getTimestamp().toString());
            item.put("viaturaId", alerta.getViatura() != null ? alerta.getViatura().getCodigo() : null);
            item.put("matricula", alerta.getViatura() != null ? alerta.getViatura().getMatricula() : null);

            resposta.add(item);
        }

        return resposta;
    }

    private String formatarEstado(String estado) {
        if (estado == null) {
            return "Indeterminado";
        }

        if (estado.equalsIgnoreCase("PENDENTE")) {
            return "Pendente";
        }

        if (estado.equalsIgnoreCase("EM_TRATAMENTO")) {
            return "Em Análise";
        }

        if (estado.equalsIgnoreCase("RESOLVIDO")) {
            return "Resolvido";
        }

        return estado;
    }

    private String calcularPrioridade(String severidade) {
        if (severidade == null) {
            return "MEDIA";
        }

        if (severidade.equalsIgnoreCase("CRÍTICO") || severidade.equalsIgnoreCase("CRITICO")) {
            return "ALTA";
        }

        return "MEDIA";
    }
}
