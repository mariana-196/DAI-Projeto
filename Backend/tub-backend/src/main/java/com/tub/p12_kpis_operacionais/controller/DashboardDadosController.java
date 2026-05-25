package com.tub.p12_kpis_operacionais.controller;

import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p5_lotacao.repository.ViaturasRepository;

import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import com.tub.p11_gestao_alertas.model.AlertaLotacao;
import com.tub.p11_gestao_alertas.repository.AlertaLotacaoRepository;

import org.springframework.web.bind.annotation.*;

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
    private final AlertaLotacaoRepository alertaLotacaoRepository;

    public DashboardDadosController(
            ViaturasRepository viaturasRepository,
            RegistoBilheticaRepository registoBilheticaRepository,
            AlertaLotacaoRepository alertaLotacaoRepository
    ) {
        this.viaturasRepository = viaturasRepository;
        this.registoBilheticaRepository = registoBilheticaRepository;
        this.alertaLotacaoRepository = alertaLotacaoRepository;
    }

    @GetMapping("/dashboard/kpis")
    public Map<String, Object> obterKpisDashboard() {
        List<Viatura> viaturas = viaturasRepository.findAll();
        List<RegistoBilhetica> registos = registoBilheticaRepository.findAll();
        List<AlertaLotacao> alertas = alertaLotacaoRepository.findAll();

        long totalViaturas = viaturas.size();
        long viaturasAtivas = viaturas.stream()
                .filter(Viatura::isAtiva)
                .count();

        int totalValidacoes = 0;
        for (RegistoBilhetica registo : registos) {
            if (registo.getValidacoes() != null) {
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
        List<AlertaLotacao> alertas = alertaLotacaoRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (AlertaLotacao alerta : alertas) {
            Map<String, Object> item = new HashMap<>();

            item.put("id", alerta.getId());
            String desc = alerta.getDescricao() != null ? alerta.getDescricao().toLowerCase() : "";
            String titulo = "Alerta Operacional";
            if (desc.contains("lotação") || desc.contains("lotacao")) {
                titulo = "Lotação Crítica (IoT)";
            } else if (desc.contains("painel") || desc.contains("dms")) {
                titulo = "Falha de Painel DMS";
            } else if (desc.contains("gps") || desc.contains("sinal")) {
                titulo = "Perda de Sinal GPS";
            }
            item.put("titulo", titulo);
            item.put("estado", formatarEstado(alerta.getEstado()));
            item.put("descricao", alerta.getDescricao());
            item.put("prioridade", calcularPrioridade(alerta.getSeveridade()));

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
