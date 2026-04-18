package com.tub.service;

import org.springframework.stereotype.Service;
import com.tub.model.ParametrosAnalise;
import com.tub.model.ResultadoAnalitico;

@Service
public class MotorCalculoAnaliticoService {

    public ResultadoAnalitico calcular(ParametrosAnalise parametros) {

        ResultadoAnalitico resultado = new ResultadoAnalitico();

        // 🔹 Simulação (como no DashboardService)
        double taxa = Math.random() * 100;
        int passageiros = (int)(Math.random() * 1000);

        resultado.setTaxaOcupacaoMedia(taxa);
        resultado.setTotalPassageiros(passageiros);

        return resultado;
    }
}