package com.tub.service;

import com.tub.model.ResultadoIndicadoresFrota;
import com.tub.model.ResumoEstadoOperacao;
import org.springframework.stereotype.Service;
import com.tub.model.ResultadoIndicadoresBilhetica;
import java.util.*;

@Service
public class DashboardService {

    public ResultadoIndicadoresBilhetica obterIndicadoresBilhetica() {
        // 1. Categorizar títulos de transporte (Simulação da agregação pedida na UC 5.2.3.c)
        int validacoesNormal = 8500;
        int validacoesEstudante = 3200;
        int validacoesSenior = 750;
        
        int totalValidacoes = validacoesNormal + validacoesEstudante + validacoesSenior;

        // Estruturar os perfis de passageiros
        Map<String, Integer> perfis = new HashMap<>();
        perfis.put("Bilhete Normal", validacoesNormal);
        perfis.put("Passe Estudante", validacoesEstudante);
        perfis.put("Passe Sénior", validacoesSenior);

        // 2. Agregar por período/linha (Simulando o pico de procura)
        String pico = "08:00 - 09:30 (Linha 43 e Linha 2)";

        // 3. Estimar Receita (A regra de negócio: Normal paga X, passes têm outro valor/subsídio)
        double precoBilheteNormal = 1.55;
        double receitaEstimada = validacoesNormal * precoBilheteNormal; 
        // Nota: Estudantes e Seniores assumidos como passe pago a priori, por isso a estimativa do dia foca nos bilhetes normais.

        // Retorna a entidade estruturada da Linha 79
        return new ResultadoIndicadoresBilhetica(
            totalValidacoes, 
            perfis, 
            pico, 
            receitaEstimada
        );
    }



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