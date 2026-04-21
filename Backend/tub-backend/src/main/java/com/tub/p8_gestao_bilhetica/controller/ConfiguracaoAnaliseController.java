package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.*;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnaliticoService;

@RestController
@RequestMapping("/analise")
public class ConfiguracaoAnaliseController {

    private final MotorCalculoAnaliticoService motorService;

    public ConfiguracaoAnaliseController(MotorCalculoAnaliticoService motorService) {
        this.motorService = motorService;
    }

    @PostMapping("/calcular")
    public ResultadoAnalitico calcular(@RequestBody ParametrosAnalise parametros) {
        return motorService.calcular(parametros);
    }
}