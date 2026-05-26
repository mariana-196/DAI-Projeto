package com.tub.p12_kpis_operacionais.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bilhetica/indicadores")
@CrossOrigin(origins = "*")
public class ControladorIndicadoresBilhetica {

    private final DashboardDadosController dashboardController;

    public ControladorIndicadoresBilhetica(DashboardDadosController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @GetMapping
    public Map<String, Object> obterIndicadoresBilhetica() {
        return dashboardController.obterKpisBilhetica(); // Método que retorna KPIs da bilhética
    }
}

// Simple local interface to satisfy compilation when the real DashboardDadosController
// is not available in this module. The real implementation (if present) should
// implement this interface.
interface DashboardDadosController {
    Map<String, Object> obterKpisBilhetica();
}