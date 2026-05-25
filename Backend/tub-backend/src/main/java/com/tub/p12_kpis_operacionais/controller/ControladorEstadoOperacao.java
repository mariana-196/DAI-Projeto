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
import com.tub.p11_gestao_alertas.model.AlertaOperacional;
import com.tub.p11_gestao_alertas.repository.AlertaOperacionalRepository;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ControladorEstadoOperacao {

    private final ProcessadorTriagemAlertas alertCenterService;
    private final ServicoEstadoOperacao dashboardService;
    private final MotorCalculoAnalitico analiseService;
    private final LotacaoViaturaRepository lotacaoViaturaRepository;
    private final DisplayPanelRepository displayPanelRepository;
    private final AlertaOperacionalRepository alertaOperacionalRepository;

    // CONSTRUTOR UNIFICADO (Resolve o conflito das dependências)
    public ControladorEstadoOperacao( ProcessadorTriagemAlertas alertCenterService, 
                               ServicoEstadoOperacao dashboardService, 
                               MotorCalculoAnalitico analiseService,
                               LotacaoViaturaRepository lotacaoViaturaRepository,
                               DisplayPanelRepository displayPanelRepository,
                               AlertaOperacionalRepository alertaOperacionalRepository) {
        this.alertCenterService = alertCenterService;
        this.dashboardService = dashboardService;
        this.analiseService = analiseService;
        this.lotacaoViaturaRepository = lotacaoViaturaRepository;
        this.displayPanelRepository = displayPanelRepository;
        this.alertaOperacionalRepository = alertaOperacionalRepository;
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
    public ResponseEntity<?> getDetalheAlerta(@PathVariable Long id) {
        Optional<AlertaOperacional> alertaOpt = alertaOperacionalRepository.findById(id);
        if (alertaOpt.isPresent()) {
            AlertaOperacional alerta = alertaOpt.get();
            Map<String, Object> detalhe = new HashMap<>();
            detalhe.put("id", alerta.getId());
            detalhe.put("titulo", alerta.getTitulo());
            detalhe.put("tema", alerta.getTema());
            detalhe.put("descricao", alerta.getDescricao());
            detalhe.put("origem", alerta.getOrigem());
            detalhe.put("timestamp", alerta.getTimestamp().toString());
            detalhe.put("prioridade", calcularPrioridade(alerta.getSeveridade()));
            detalhe.put("estado", formatarEstado(alerta.getEstado()));
            detalhe.put("infoAdicional", alerta.getInfoAdicional() != null ? alerta.getInfoAdicional() : "Sem metadados adicionais");
            detalhe.put("linha", alerta.getLinha());
            detalhe.put("historico", alerta.getHistorico());
            
            if (alerta.getViatura() != null) {
                Map<String, Object> viatInfo = new HashMap<>();
                viatInfo.put("codigo", alerta.getViatura().getCodigo());
                viatInfo.put("matricula", alerta.getViatura().getMatricula());
                viatInfo.put("modelo", alerta.getViatura().getModelo());
                viatInfo.put("capacidadeMaxima", alerta.getViatura().getCapacidadeMaxima());
                detalhe.put("viatura", viatInfo);
            } else {
                detalhe.put("viatura", null);
            }
            
            return ResponseEntity.ok(detalhe);
        }
        return ResponseEntity.notFound().build();
    }

    // --- LINHA 86: GESTÃO/PERSISTÊNCIA DO ALERTA ---
    @PutMapping({"/alertas/{id}", "/alerts/{id}"})
    public ResponseEntity<Map<String, String>> atualizarAlerta(
            @PathVariable Long id, 
            @RequestBody Map<String, String> dados
    ) {
        String novoEstado = dados.get("estado");
        String comentario = dados.get("comentario");
        System.out.println("Alerta " + id + " atualizado para: " + novoEstado);
        
        Optional<AlertaOperacional> alertaOpt = alertaOperacionalRepository.findById(id);
        Map<String, String> res = new HashMap<>();
        
        if (alertaOpt.isPresent()) {
            AlertaOperacional alerta = alertaOpt.get();
            alerta.setEstado(novoEstado);
            
            String logMsg = "Estado alterado para " + formatarEstado(novoEstado) + " por Operador CCO.";
            if (comentario != null && !comentario.trim().isEmpty()) {
                logMsg += " Comentário: \"" + comentario.trim() + "\"";
            }
            alerta.adicionarLogHistorico(logMsg);
            
            alertaOperacionalRepository.save(alerta);
            res.put("status", "Sucesso");
            return ResponseEntity.ok(res);
        }
        
        res.put("status", "Erro");
        res.put("mensagem", "Alerta não encontrado.");
        return ResponseEntity.status(404).body(res);
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