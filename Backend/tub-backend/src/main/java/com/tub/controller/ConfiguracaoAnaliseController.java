package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import com.tub.model.ParametrosAnalise;
import com.tub.model.ResultadoAnalitico;
import com.tub.service.MotorCalculoAnaliticoService;

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