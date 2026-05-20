package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.*;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnalitico;

@RestController
@RequestMapping("/analise")
@CrossOrigin(origins = "*")
public class ControladorConfiguracaoAnalise {

    private final MotorCalculoAnalitico motorService;

    public ControladorConfiguracaoAnalise(MotorCalculoAnalitico motorService) {
        this.motorService = motorService;
    }

    @PostMapping("/calcular")
    public ResultadoAnalitico calcular(@RequestBody ParametrosAnalise parametros) {
        return motorService.calcular(parametros);
    }
}