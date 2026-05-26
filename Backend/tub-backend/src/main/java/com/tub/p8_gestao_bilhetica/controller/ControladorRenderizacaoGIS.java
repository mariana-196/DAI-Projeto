package com.tub.p8_gestao_bilhetica.controller;

import com.tub.p1_autenticacao.annotation.RequerCargo;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;
import com.tub.p8_gestao_bilhetica.model.DatasetGeoJSON;
import com.tub.p8_gestao_bilhetica.service.MotorInferenciaEspacial;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ControladorRenderizacaoGIS {

    private final MotorInferenciaEspacial motorEspacial;
    private final ControloConsultaAuditoria auditService;

    public ControladorRenderizacaoGIS(MotorInferenciaEspacial motorEspacial, ControloConsultaAuditoria auditService) {
        this.motorEspacial = motorEspacial;
        this.auditService = auditService;
    }

    @GetMapping("/gis/render/dados-mapa")
    @RequerCargo({"OPERADOR", "ADMINISTRADOR"})
    public DatasetGeoJSON obterDadosParaMapa(
            @RequestParam(defaultValue = "heatmap") String tipoMapa,
            @RequestParam(required = false) String linha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer horaInicio,
            @RequestParam(required = false) Integer horaFim,
            HttpServletRequest request
    ) {
        DatasetGeoJSON dados = motorEspacial.gerarDadosEspaciais(tipoMapa, linha, dataInicio, dataFim, horaInicio, horaFim);
        registarAuditoriaMapa(request, tipoMapa, linha, dataInicio, dataFim, horaInicio, horaFim);
        return dados;
    }

    @GetMapping("/bilhetica/mapa")
    @RequerCargo({"OPERADOR", "ADMINISTRADOR"})
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<Map<String, Object>>> obterPontosMapa() {
        DatasetGeoJSON geojson = motorEspacial.gerarDadosEspaciais();
        List<Map<String, Object>> pontos = new ArrayList<>();

        for (Map<String, Object> feature : geojson.getFeatures()) {
            Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
            double[] coords = (double[]) geometry.get("coordinates");
            Map<String, Object> properties = (Map<String, Object>) feature.get("properties");

            if (!"heatmap".equals(properties.get("tipo"))) {
                continue;
            }

            String nome = (String) properties.get("nome");
            int totalValidacoes = (int) properties.get("totalValidacoes");
            boolean hotspot = (boolean) properties.get("hotspot");

            Map<String, Object> ponto = new HashMap<>();
            ponto.put("nome", nome);
            ponto.put("lat", coords[1]);
            ponto.put("lng", coords[0]);
            ponto.put("cor", hotspot ? "red" : "blue");
            ponto.put("raio", Math.min(500, 100 + totalValidacoes * 5));

            pontos.add(ponto);
        }

        return ResponseEntity.ok(pontos);
    }

    private void registarAuditoriaMapa(
            HttpServletRequest request,
            String tipoMapa,
            String linha,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer horaInicio,
            Integer horaFim
    ) {
        try {
            String email = (String) request.getAttribute("utilizador_email");
            auditService.registar(
                    email != null ? email : "Sistema",
                    "GERAR_MAPA_FLUXOS",
                    "Bilhetica",
                    request.getRemoteAddr(),
                    "INFO",
                    "Mapa de fluxos gerado. Tipo=" + tipoMapa + ", linha=" + linha
                            + ", dataInicio=" + dataInicio + ", dataFim=" + dataFim
                            + ", horaInicio=" + horaInicio + ", horaFim=" + horaFim
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }
    }
}
