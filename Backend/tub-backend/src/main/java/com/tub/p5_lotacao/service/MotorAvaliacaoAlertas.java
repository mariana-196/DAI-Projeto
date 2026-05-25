package com.tub.p5_lotacao.service;

import com.tub.p11_gestao_alertas.model.AlertaOperacional;
import com.tub.p11_gestao_alertas.repository.AlertaOperacionalRepository;
import com.tub.p5_lotacao.model.RegraAlerta;
import com.tub.p5_lotacao.repository.RegraAlertaRepository;
import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotorAvaliacaoAlertas {

    @Autowired
    private AlertaOperacionalRepository alertaRepository;

    @Autowired
    private RegraAlertaRepository regraRepository;

    public void verificarEAplicarAlerta(EstadoOcupacaoViatura lotacao) {
        List<RegraAlerta> regrasAtivas = regraRepository.findAll().stream()
                .filter(RegraAlerta::isAtivo)
                .collect(Collectors.toList());

        for (RegraAlerta regra : regrasAtivas) {
            if (lotacao.getTaxaOcupacao() >= regra.getLimite()) {

                AlertaOperacional novoAlerta = new AlertaOperacional(
                    lotacao.getViatura(),
                    lotacao.getLinha(),
                    "Lotação Crítica (IoT)",
                    "LOTACAO",
                    "CRITICO",
                    "PENDENTE",
                    regra.getDescricao() + " - Lotação atingiu " + String.format("%.1f", lotacao.getTaxaOcupacao()) + "%",
                    "IoT Sensores",
                    "Limite configurado: " + regra.getLimite() + "%. Passageiros: " + lotacao.getPassageirosAtuais() + "/" + lotacao.getViatura().getCapacidadeMaxima()
                );

                alertaRepository.save(novoAlerta);

                System.out.println(">>> [MOTOR] Sucesso: Alerta registado para a viatura ID: " + lotacao.getViatura().getId());
            }
        }
    }
}