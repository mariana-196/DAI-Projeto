package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import java.util.List;

@Service
public class MotorCalculoAnalitico {

    private final RegistoBilheticaRepository registoRepository;

    // 1. Injetar o repositório no construtor para podermos ir à Base de Dados
    public MotorCalculoAnalitico(RegistoBilheticaRepository registoRepository) {
        this.registoRepository = registoRepository;
    }

    public ResultadoAnalitico calcular(ParametrosAnalise parametros) {
        ResultadoAnalitico resultado = new ResultadoAnalitico();

        List<RegistoBilhetica> todosOsRegistos = registoRepository.findAll();

        // Filter by line ID or line code if specified
        if (parametros.getLinhaId() != null) {
            String codigoStr = String.valueOf(parametros.getLinhaId());
            todosOsRegistos = todosOsRegistos.stream()
                    .filter(r -> r.getLinha() != null && 
                        (r.getLinha().getId().equals(parametros.getLinhaId()) || 
                         r.getLinha().getCodigo().equals(codigoStr)))
                    .toList();
        }

        int totalPassageiros = 0;
        double somaTaxas = 0.0;
        int countComCapacidade = 0;

        for (RegistoBilhetica registo : todosOsRegistos) {
            if (registo.getValidacoes() != null) {
                totalPassageiros += registo.getValidacoes();
                
                int capacidade = 80; // default capacity
                if (registo.getViatura() != null && registo.getViatura().getCapacidadeMaxima() != null && registo.getViatura().getCapacidadeMaxima() > 0) {
                    capacidade = registo.getViatura().getCapacidadeMaxima();
                }
                
                somaTaxas += ((double) registo.getValidacoes() / capacidade) * 100;
                countComCapacidade++;
            }
        }

        double taxaOcupacao = 0.0;
        if (countComCapacidade > 0) {
            taxaOcupacao = somaTaxas / countComCapacidade;
        }

        resultado.setTaxaOcupacaoMedia(taxaOcupacao);
        resultado.setTotalPassageiros(totalPassageiros);

        return resultado;
    }
}