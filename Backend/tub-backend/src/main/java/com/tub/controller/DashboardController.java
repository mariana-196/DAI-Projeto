package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;

import com.tub.service.AlertCenterService;
import com.tub.service.DashboardService;
import com.tub.service.MotorCalculoAnaliticoService;
import com.tub.model.AlertaUnificado;
import com.tub.model.ResultadoIndicadoresBilhetica;
import com.tub.model.ResultadoIndicadoresFrota;
import com.tub.model.ResumoEstadoOperacao;
import com.tub.model.ContextoAlerta;
import com.tub.model.ParametrosAnalise;
import com.tub.model.ResultadoAnalitico;
import com.tub.model.RelatorioExportacao;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class DashboardController {

    private final AlertCenterService alertCenterService;
    private final DashboardService dashboardService;
    private final MotorCalculoAnaliticoService analiseService;

    // CONSTRUTOR UNIFICADO (Resolve o conflito das dependências)
    public DashboardController(AlertCenterService alertCenterService, 
                               DashboardService dashboardService, 
                               MotorCalculoAnaliticoService analiseService) {
        this.alertCenterService = alertCenterService;
        this.dashboardService = dashboardService;
        this.analiseService = analiseService;
    }

    // --- LINHA 43: ANÁLISE DE RESULTADOS ---
    @PostMapping("/dashboard/analise-resultados")
    public ResultadoAnalitico obterResultadosAnalise(@RequestBody ParametrosAnalise params) {
        return analiseService.calcular(params);
    }

    // --- LINHA 45: EXPORTAÇÃO ---
    @PostMapping("/dashboard/exportar")
    public RelatorioExportacao exportarRelatorio(@RequestBody ParametrosAnalise params) {
        ResultadoAnalitico resultado = analiseService.calcular(params);
        RelatorioExportacao relatorio = new RelatorioExportacao();
        relatorio.setTitulo("Relatório de Performance - TUB");
        relatorio.setDataGeracao(LocalDateTime.now());
        
        Map<String, Object> metricas = new HashMap<>();
        metricas.put("totalValidacoes", resultado.getTotalPassageiros());
        relatorio.setMetricas(metricas);
        
        return relatorio;
    }

    // --- INDICADORES (LINHAS 72, 78, 81) ---
    @GetMapping("/dashboard/estado-geral")
    public ResumoEstadoOperacao getEstadoGeral() {
        return dashboardService.obterIndicadoresReais();
    }

    @GetMapping("/dashboard/indicadores-frota")
    public ResultadoIndicadoresFrota getIndicadoresFrota() {
        return dashboardService.obterIndicadoresFrota();
    }

    @GetMapping("/dashboard/indicadores-bilhetica")
    public ResultadoIndicadoresBilhetica getIndicadoresBilhetica() {
        return dashboardService.obterIndicadoresBilhetica();
    }

    @GetMapping("/alertas/v2")
    public List<AlertaUnificado> getAlertasUnificados() {
        return alertCenterService.triagemDeAlertas();
    }

    // --- LINHA 88: DETALHE DO ALERTA ---
    @GetMapping("/alertas/{id}/detalhe")
    public ContextoAlerta getDetalheAlerta(@PathVariable Long id) {
        List<String> historico = Arrays.asList(
            "2023-10-27 10:00 - Gerado pelo Sistema",
            "2023-10-27 10:15 - Prioridade atualizada por CCO"
        );
        return new ContextoAlerta(id, "Motor Sobreaquecido", "Viatura #405 a 105°C.", "Wavecom IoT", "ALTA", "Pendente", "Local: Variante", historico);
    }

    // --- LINHA 86: GESTÃO/PERSISTÊNCIA DO ALERTA ---
    @PutMapping("/alertas/{id}")
    public Map<String, String> atualizarAlerta(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        System.out.println("Alerta " + id + " atualizado para: " + dados.get("estado"));
        Map<String, String> res = new HashMap<>();
        res.put("status", "Sucesso");
        return res;
    }

    // --- AUXILIARES ---
    @GetMapping("/frota/posicoes")
    public List<Map<String, Object>> getPosicoes() {
        List<Map<String, Object>> frota = new ArrayList<>();
        Map<String, Object> v1 = new HashMap<>();
        v1.put("id", 112); v1.put("lat", 41.5510); v1.put("lng", -8.4220);
        frota.add(v1);
        return frota;
    }

    private void addAlerta(List<Map<String, Object>> list, int id, String titulo, String desc, String prioridade, String estado) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("titulo", titulo);
        item.put("descricao", desc);
        item.put("prioridade", prioridade);
        item.put("estado", estado);
        list.add(item);
    }
}