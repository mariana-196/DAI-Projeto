package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnalitico;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorDashboard {

    private final MotorCalculoAnalitico motorCalculoAnalitico;

    public ControladorDashboard(MotorCalculoAnalitico motorCalculoAnalitico) {
        this.motorCalculoAnalitico = motorCalculoAnalitico;
    }

    @GetMapping("/dashboard/resultados")
    public ResultadoAnalitico obterResultados(
            @RequestParam(required = false) Long linhaId,
            @RequestParam(required = false) String periodo
    ) {
        ParametrosAnalise parametros = new ParametrosAnalise();
        parametros.setLinhaId(linhaId);
        parametros.setPeriodo(periodo);

        return motorCalculoAnalitico.calcular(parametros);
    }

    @GetMapping("/analise")
    public org.springframework.http.ResponseEntity<?> obterAnalise(@RequestParam(required = false) String linha) {
        ParametrosAnalise parametros = new ParametrosAnalise();
        if (linha != null && !linha.isEmpty() && !linha.equals("vazia")) {
            try {
                parametros.setLinhaId(Long.parseLong(linha));
            } catch (NumberFormatException e) {
                // ignora ou loga
            }
        }
        ResultadoAnalitico res = motorCalculoAnalitico.calcular(parametros);
        int total = res.getTotalPassageiros();

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("taxaOcupacaoMedia", res.getTaxaOcupacaoMedia());
        map.put("totalPassageiros", total);

        java.util.List<Integer> procura = java.util.Arrays.asList(
            (int) (total * 0.15),
            (int) (total * 0.35),
            (int) (total * 0.25),
            (int) (total * 0.15),
            (int) (total * 0.10)
        );
        map.put("procura", procura);

        return org.springframework.http.ResponseEntity.ok(map);
    }
}