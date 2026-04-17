package com.tub.service;

import com.tub.model.ResultadoIndicadoresFrota;
import com.tub.model.ResumoEstadoOperacao;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    // Adiciona este método dentro do teu DashboardService.java
public ResultadoIndicadoresFrota obterIndicadoresFrota() {
    // Dados base para o cálculo
    int totalFrota = 120;
    int emManutencao = 8;
    int semComunicacao = 5; // Requisito: Identificar viaturas sem sinal

    // Lógica: Viaturas "Ativas" são as que têm sinal e não estão em oficina
    int ativas = totalFrota - emManutencao - semComunicacao;

    // Cálculo da taxa de disponibilidade (Requisito da Linha 75)
    double taxa = ((double) ativas / totalFrota) * 100;

    return new ResultadoIndicadoresFrota(
        taxa, 
        ativas, 
        emManutencao, 
        semComunicacao, 
        31.8 // Consumo médio simulado
    );
}

    public ResumoEstadoOperacao obterIndicadoresReais() {
        // Dados simulados (a substituir por BD no futuro)
        int servicosAtivos = 84; 
        int atrasos = 12;
        int interrupcoes = 3;
        int criticos = 2; // ex: sensores de motor

        // A Lógica de Agrupamento pedida na Linha 72
        String gravidade;
        if (interrupcoes > 3 || criticos > 5) {
            gravidade = "CRÍTICA";
        } else if (atrasos > 10 || interrupcoes > 0) {
            gravidade = "MODERADA";
        } else {
            gravidade = "ESTÁVEL";
        }

        // Retorna o objeto da Linha 73 preenchido
        return new ResumoEstadoOperacao(
            servicosAtivos, 
            atrasos, 
            interrupcoes, 
            criticos, 
            1, // moderados simulados
            0, // ligeiros simulados
            gravidade
        );
    }
}