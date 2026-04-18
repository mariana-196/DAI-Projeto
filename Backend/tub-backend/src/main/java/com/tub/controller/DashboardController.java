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
import com.tub.model.ParametrosAnalise;
import com.tub.model.ResultadoAnalitico;
import com.tub.model.RelatorioExportacao;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite a ligação com o teu frontend HTML/JS
public class DashboardController {

    private final AlertCenterService alertCenterService;
    private final DashboardService dashboardService;
    private final MotorCalculoAnaliticoService analiseService;

    // Construtor: Injeta todas as dependências necessárias de uma só vez
    public DashboardController(AlertCenterService alertCenterService, 
                               DashboardService dashboardService, 
                               MotorCalculoAnaliticoService analiseService) {
        this.alertCenterService = alertCenterService;
        this.dashboardService = dashboardService;
        this.analiseService = analiseService;
    }

    // --- LINHA 43: DASHBOARD DE RESULTADOS ANALÍTICOS ---
    @PostMapping("/dashboard/analise-resultados")
    public ResultadoAnalitico obterResultadosAnalise(@RequestBody ParametrosAnalise params) {
        // Chama a lógica de cálculo real que consome os dados do RegistoBilheticaRepository
        return analiseService.calcular(params);
    }

    // --- LINHA 45: EXPORTAÇÃO DE RELATÓRIO ---
    @PostMapping("/dashboard/exportar")
    public RelatorioExportacao exportarRelatorio(@RequestBody ParametrosAnalise params) {
        ResultadoAnalitico resultado = analiseService.calcular(params);
        
        RelatorioExportacao relatorio = new RelatorioExportacao();
        relatorio.setTitulo("Relatório de Performance de Bilhética - TUB");
        relatorio.setDataGeracao(LocalDateTime.now());
        
        Map<String, Object> metricas = new HashMap<>();
        metricas.put("totalValidacoes", resultado.getTotalPassageiros());
        metricas.put("ocupacaoMedia", resultado.getTaxaOcupacaoMedia());
        relatorio.setMetricas(metricas);
        
        return relatorio;
    }

    // --- ENDPOINTS DE INDICADORES (LINHAS 72, 78, 81) ---

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

    // --- ENDPOINTS DE SUPORTE AO DASHBOARD (FRONTEND) ---

    @GetMapping("/dashboard/kpis")
    public Map<String, Object> getKpis() {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("ativas", 84);
        kpis.put("total", 90);
        kpis.put("pontualidade", 92);
        kpis.put("validacoes", 12450);
        return kpis;
    }

    @GetMapping("/frota/posicoes")
    public List<Map<String, Object>> getPosicoes() {
        List<Map<String, Object>> frota = new ArrayList<>();
        
        // Exemplo: Viatura #112
        Map<String, Object> v1 = new HashMap<>();
        v1.put("id", 112);
        v1.put("linha", "43");
        v1.put("lat", 41.5510);
        v1.put("lng", -8.4220);
        v1.put("status", "Em circulação");
        v1.put("sinal", true);
        frota.add(v1);

        // Exemplo: Viatura #405
        Map<String, Object> v2 = new HashMap<>();
        v2.put("id", 405);
        v2.put("linha", "2");
        v2.put("lat", 41.5450);
        v2.put("lng", -8.4100);
        v2.put("status", "Sinal OK");
        v2.put("sinal", true);
        frota.add(v2);

        return frota;
    }

    @GetMapping("/alertas")
    public List<Map<String, Object>> getAlertas() {
        List<Map<String, Object>> alertas = new ArrayList<>();
        addAlerta(alertas, 1, "Motor Sobreaquecido", "Autocarro #405 (Linha 2). Sensores IoT mecânicos.", "ALTA", "Pendente");
        addAlerta(alertas, 2, "Erro de Leitura", "Dados ilegíveis da Bilhética Externa.", "BAIXA", "Indeterminado");
        return alertas;
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