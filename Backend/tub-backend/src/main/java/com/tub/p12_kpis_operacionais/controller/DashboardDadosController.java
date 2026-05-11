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

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("ativas", viaturasAtivas);
        kpis.put("total", totalViaturas);
        kpis.put("pontualidade", 92);
        kpis.put("validacoes", totalValidacoes);

        return kpis;
    }

    @GetMapping("/alertas")
    public List<Map<String, Object>> obterAlertasDashboard() {
        List<AlertaLotacao> alertas = alertaLotacaoRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (AlertaLotacao alerta : alertas) {
            Map<String, Object> item = new HashMap<>();

            item.put("id", alerta.getId());
            item.put("titulo", "Lotação Crítica (IoT)");
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
