package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.*;

import com.tub.p8_gestao_bilhetica.model.DatasetGeoJSON;
import com.tub.p8_gestao_bilhetica.service.MotorInferenciaEspacial;

@RestController
@RequestMapping("/api/gis/render")
@CrossOrigin(origins = "*") // Importante para o Frontend conseguir ler os dados
public class ControladorRenderizacaoGIS {

    private final MotorInferenciaEspacial motorEspacial;
    public ControladorRenderizacaoGIS(MotorInferenciaEspacial motorEspacial) {
        this.motorEspacial = motorEspacial;
    }

    @GetMapping("/dados-mapa")
    public DatasetGeoJSON obterDadosParaMapa() {
        // Chama o serviço que criámos para Braga
        return motorEspacial.gerarDadosEspaciais();
    }
}