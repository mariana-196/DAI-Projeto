package com.tub.p12_kpis_operacionais.controller;

import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;
import com.tub.p9_monitorizacao_iot.repository.LotacaoViaturaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;

import com.tub.p11_gestao_alertas.model.AlertaUnificado;
import com.tub.p11_gestao_alertas.model.ContextoAlerta;
import com.tub.p11_gestao_alertas.service.ProcessadorTriagemAlertas;
import com.tub.p12_kpis_operacionais.model.ResultadoIndicadoresBilhetica;
import com.tub.p12_kpis_operacionais.model.ResultadoIndicadoresFrota;
import com.tub.p12_kpis_operacionais.model.ResumoEstadoOperacao;
import com.tub.p12_kpis_operacionais.service.ServicoEstadoOperacao;
import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnalitico;
import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;
import com.tub.p11_gestao_alertas.model.AlertaLotacao;
import com.tub.p11_gestao_alertas.repository.AlertaLotacaoRepository;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ControladorEstadoOperacao {

    private final ProcessadorTriagemAlertas alertCenterService;
    private final ServicoEstadoOperacao dashboardService;
    private final MotorCalculoAnalitico analiseService;
    private final LotacaoViaturaRepository lotacaoViaturaRepository;
    private final DisplayPanelRepository displayPanelRepository;
    private final AlertaLotacaoRepository alertaLotacaoRepository;

    // CONSTRUTOR UNIFICADO (Resolve o conflito das dependências)
    public ControladorEstadoOperacao( ProcessadorTriagemAlertas alertCenterService, 
                               ServicoEstadoOperacao dashboardService, 
                               MotorCalculoAnalitico analiseService,
                               LotacaoViaturaRepository lotacaoViaturaRepository,
                               DisplayPanelRepository displayPanelRepository,
                               AlertaLotacaoRepository alertaLotacaoRepository) {
        this.alertCenterService = alertCenterService;
        this.dashboardService = dashboardService;
        this.analiseService = analiseService;
        this.lotacaoViaturaRepository = lotacaoViaturaRepository;
        this.displayPanelRepository = displayPanelRepository;
        this.alertaLotacaoRepository = alertaLotacaoRepository;
    }

    // --- LINHA 43: ANÁLISE DE RESULTADOS ---
    @PostMapping("/dashboard/analise-resultados")
    public ResultadoAnalitico obterResultadosAnalise(@RequestBody ParametrosAnalise params) {
        return analiseService.calcular(params);
    }

    

    // --- INDICADORES (LINHAS 72, 78, 81) ---
    @GetMapping("/dashboard/estado-geral")
    public ResumoEstadoOperacao getEstadoGeral() {
        return dashboardService.obterIndicadoresReais();
    }

    @GetMapping("/indicadores-frota")
    public ResultadoIndicadoresFrota controladorIndicadoresFrota() {
        return dashboardService.obterIndicadoresFrota();
    }

    @GetMapping("/indicadores-bilhetica")
    public ResultadoIndicadoresBilhetica controladorIndicadoresBilhetica() {
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
    @PutMapping({"/alertas/{id}", "/alerts/{id}"})
    public Map<String, String> atualizarAlerta(@PathVariable Long id, @RequestBody Map<String, String> dados) {
        System.out.println("Alerta " + id + " atualizado para: " + dados.get("estado"));
        Optional<AlertaLotacao> alertaOpt = alertaLotacaoRepository.findById(id);
        if (alertaOpt.isPresent()) {
            AlertaLotacao alerta = alertaOpt.get();
            alerta.setEstado(dados.get("estado"));
            alertaLotacaoRepository.save(alerta);
        }
        Map<String, String> res = new HashMap<>();
        res.put("status", "Sucesso");
        return res;
    }

    // --- AUXILIARES ---
   @GetMapping("/frota/posicoes")
public List<Map<String, Object>> getPosicoes() {
    List<EstadoOcupacaoViatura> estados = lotacaoViaturaRepository.findAll();
    List<Map<String, Object>> frota = new ArrayList<>();

    double[][] coordenadasDemo = {
            {41.5503, -8.4200},
            {41.5520, -8.4210},
            {41.5550, -8.3970},
            {41.5670, -8.3990},
            {41.5490, -8.4340},
            {41.5545, -8.3775}
    };

    int i = 0;

    for (EstadoOcupacaoViatura estado : estados) {
        Map<String, Object> viatura = new HashMap<>();

        double[] coordenadas = coordenadasDemo[i % coordenadasDemo.length];

        viatura.put("id", estado.getViatura().getCodigo());
        viatura.put("lat", coordenadas[0]);
        viatura.put("lng", coordenadas[1]);
        viatura.put("linha", estado.getLinha());
        viatura.put("status", estado.isSinalAtivo() ? "Em Horário" : "Sem Sinal");
        viatura.put("velocidade", estado.isSinalAtivo() ? 42 : 0);
        viatura.put("lotacao", Math.round(estado.getTaxaOcupacao()));
        viatura.put("sinal", estado.isSinalAtivo());

        frota.add(viatura);
        i++;
    }

    return frota;
}

    @GetMapping("/paragens")
    public List<Map<String, Object>> getParagens() {
        List<DisplayPanel> paineis = displayPanelRepository.findAll();
        List<Map<String, Object>> paragens = new ArrayList<>();
        
        for (DisplayPanel painel : paineis) {
            Map<String, Object> p = new HashMap<>();
            p.put("id", painel.getPanelId());
            p.put("nome", painel.getLocation());
            p.put("mensagem", painel.getMessage());
            p.put("estado", painel.getStatus());
            
            // Determinar coordenadas correspondentes em Braga
            double lat = 41.5503;
            double lng = -8.4200;
            String loc = painel.getLocation().toLowerCase();
            if (loc.contains("gualtar") || loc.contains("universidade")) {
                lat = 41.5610; lng = -8.3970;
            } else if (loc.contains("central") || loc.contains("avenida")) {
                lat = 41.5515; lng = -8.4210;
            } else if (loc.contains("hospital")) {
                lat = 41.5670; lng = -8.3990;
            } else if (loc.contains("estação") || loc.contains("cp")) {
                lat = 41.5490; lng = -8.4340;
            } else if (loc.contains("bom jesus")) {
                lat = 41.5545; lng = -8.3775;
            }
            p.put("lat", lat);
            p.put("lng", lng);
            paragens.add(p);
        }
        return paragens;
    }
}