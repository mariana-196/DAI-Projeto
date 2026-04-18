package com.tub.service;

import org.springframework.stereotype.Service;
import com.tub.model.ParametrosAnalise;
import com.tub.model.ResultadoAnalitico;
import com.tub.model.RegistoBilhetica;
import com.tub.repository.RegistoBilheticaRepository;

import java.util.List;

@Service
public class MotorCalculoAnaliticoService {

    private final RegistoBilheticaRepository registoRepository;

    // 1. Injetar o repositório no construtor para podermos ir à Base de Dados
    public MotorCalculoAnaliticoService(RegistoBilheticaRepository registoRepository) {
        this.registoRepository = registoRepository;
    }

    public ResultadoAnalitico calcular(ParametrosAnalise parametros) {

        ResultadoAnalitico resultado = new ResultadoAnalitico();

        // 2. Ir buscar os registos reais à Base de Dados
        // (No futuro, podes criar um método no Repository para filtrar pelas datas que vêm nos 'parametros')
        List<RegistoBilhetica> todosOsRegistos = registoRepository.findAll();

        int totalPassageiros = 0;
        int numeroDeRegistos = todosOsRegistos.size();

        // 3. Fazer a matemática real com base nos teus dados
        for (RegistoBilhetica registo : todosOsRegistos) {
            // Somamos as validações de cada registo (usando o campo que me mostraste na tua Entity)
            if (registo.getValidacoes() != null) {
                totalPassageiros += registo.getValidacoes();
            }
        }

        // Calcular uma média (exemplo: média de passageiros por paragem/registo)
        double taxaOcupacao = 0.0;
        if (numeroDeRegistos > 0) {
            taxaOcupacao = (double) totalPassageiros / numeroDeRegistos;
        }

        // 4. Guardar os valores reais no ResultadoAnalitico
        resultado.setTaxaOcupacaoMedia(taxaOcupacao);
        resultado.setTotalPassageiros(totalPassageiros);

        return resultado;
    }
}