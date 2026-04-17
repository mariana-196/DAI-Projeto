package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.tub.service.DashboardService;
import com.tub.model.ResultadoIndicadoresFrota;
import com.tub.model.ResumoEstadoOperacao;
import org.springframework.beans.factory.annotation.Autowired;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite a ponte com o teu HTML
public class DashboardController {

    // Adiciona este endpoint dentro do teu DashboardController.java

@GetMapping("/dashboard/indicadores-frota")
public ResultadoIndicadoresFrota getIndicadoresFrota() {
    // Chama a lógica de cálculo que criámos no serviço
    return dashboardService.obterIndicadoresFrota();
}
    // --- 1. FERRAMENTA (SERVICE) ---
    // Coloca isto aqui mesmo no topo da classe
    @Autowired
    private DashboardService dashboardService;

    // --- 2. ENDPOINT DA LINHA 72 ---
    // Este método usa o Service para devolver os KPIs "reais"
    @GetMapping("/dashboard/estado-geral")
    public ResumoEstadoOperacao getEstadoGeral() {
        return dashboardService.obterIndicadoresReais();
    }

    // --- KPIs PARA O DASHBOARD ---
    @GetMapping("/dashboard/kpis")
    public Map<String, Object> getKpis() {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("ativas", 84);
        kpis.put("total", 90);
        kpis.put("pontualidade", 92);
        kpis.put("validacoes", 12450);
        return kpis;
    }

    // --- POSIÇÕES DA FROTA PARA O MAPA ---
    @GetMapping("/frota/posicoes")
    public List<Map<String, Object>> getPosicoes() {
        List<Map<String, Object>> frota = new ArrayList<>();
        
        // Viatura #112
        Map<String, Object> v1 = new HashMap<>();
        v1.put("id", 112);
        v1.put("linha", "43");
        v1.put("lat", 41.5510);
        v1.put("lng", -8.4220);
        v1.put("status", "Em circulação");
        v1.put("sinal", true);
        frota.add(v1);

        // Viatura #405
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

    // --- ALERTAS PARA O PAINEL LATERAL ---
    @GetMapping("/alertas")
    public List<Map<String, Object>> getAlertas() {
        List<Map<String, Object>> alertas = new ArrayList<>();
        
        // A CORREÇÃO ESTÁ AQUI: Chamamos o método diretamente, sem o "a1." à frente
        addAlerta(alertas, 1, "Motor Sobreaquecido", "Autocarro #405 (Linha 2). Evento recebido via sensores mecânicos IoT.", "ALTA", "Pendente");
        addAlerta(alertas, 2, "Erro de Leitura Desconhecido", "Dados ilegíveis recebidos da Bilhética Externa. Requer revisão.", "BAIXA", "Indeterminado");
        
        return alertas;
    }


    // Método auxiliar (Ponte limpa para criar os alertas)
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