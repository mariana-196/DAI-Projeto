package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnalitico;

@RestController
@RequestMapping("/api/bilhetica/dashboard")
public class ControladorDashboard {

    private final MotorCalculoAnalitico motorCalculoAnalitico;

    public ControladorDashboard(MotorCalculoAnalitico motorCalculoAnalitico) {
        this.motorCalculoAnalitico = motorCalculoAnalitico;
    }

    @GetMapping("/resultados")
    public ResultadoAnalitico obterResultados(
            @RequestParam(required = false) Long linhaId,
            @RequestParam(required = false) String periodo
    ) {
        ParametrosAnalise parametros = new ParametrosAnalise();
        parametros.setLinhaId(linhaId);
        parametros.setPeriodo(periodo);

        return motorCalculoAnalitico.calcular(parametros);
    }
}