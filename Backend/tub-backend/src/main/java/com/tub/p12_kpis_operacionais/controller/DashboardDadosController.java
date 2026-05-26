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
public class DashboardDadosController {

    private final ViaturasRepository viaturasRepository;
    private final RegistoBilheticaRepository registoBilheticaRepository;
    private final AlertaOperacionalRepository alertaOperacionalRepository;

    public DashboardDadosController(
            ViaturasRepository viaturasRepository,
            RegistoBilheticaRepository registoBilheticaRepository,
            AlertaOperacionalRepository alertaOperacionalRepository
    ) {
        this.viaturasRepository = viaturasRepository;
        this.registoBilheticaRepository = registoBilheticaRepository;
        this.alertaOperacionalRepository = alertaOperacionalRepository;
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

        LocalDateTime inicioHoje = LocalDate.now().atStartOfDay();
        LocalDateTime inicioAmanha = inicioHoje.plusDays(1);

        int totalValidacoes = 0;
        for (RegistoBilhetica registo : registos) {
            if (registo.getValidacoes() != null
                    && registo.getDataHora() != null
                    && !registo.getDataHora().isBefore(inicioHoje)
                    && registo.getDataHora().isBefore(inicioAmanha)) {
                totalValidacoes += registo.getValidacoes();
            }
        }

        long activeAlerts = alertas.stream()
                .filter(a -> a.getEstado() == null || !a.getEstado().equalsIgnoreCase("RESOLVIDO"))
                .count();

        long punctuality = 100 - (activeAlerts * 2);
        if (punctuality < 75) {
            punctuality = 75;
        } else if (punctuality > 98) {
            punctuality = 98;
        }

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("ativas", viaturasAtivas);
        kpis.put("total", totalViaturas);
        kpis.put("pontualidade", punctuality);
        kpis.put("validacoes", totalValidacoes);

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
