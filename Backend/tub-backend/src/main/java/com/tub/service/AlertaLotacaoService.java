package com.tub.service;

import com.tub.model.AlertaLotacao;
import com.tub.model.LotacaoViatura;
import com.tub.model.RegraAlerta;
import com.tub.repository.AlertaLotacaoRepository;
import com.tub.repository.RegraAlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaLotacaoService {

    @Autowired
    private AlertaLotacaoRepository alertaRepository;

    @Autowired
    private RegraAlertaRepository regraRepository;

    /**
     * Linha 61: Motor de Avaliação de Alertas corrigido para o teu Modelo
     */
    public void verificarEAplicarAlerta(LotacaoViatura lotacao) {
        // 1. Procura as regras de alerta que estão marcadas como ativas
        List<RegraAlerta> regrasAtivas = regraRepository.findAll().stream()
                .filter(RegraAlerta::isAtivo)
                .collect(Collectors.toList());

        for (RegraAlerta regra : regrasAtivas) {
            // 2. Compara a taxa de ocupação real com o limite definido na regra
            if (lotacao.getTaxaOcupacao() >= regra.getLimite()) {
                
                // 3. Cria o alerta usando os campos exatos da tua classe AlertaLotacao
                AlertaLotacao novoAlerta = new AlertaLotacao();
                
                novoAlerta.setViatura(lotacao.getViatura());
                novoAlerta.setLinha(lotacao.getLinha());
                novoAlerta.setSeveridade("CRÍTICO"); 
                novoAlerta.setEstado("PENDENTE");
                novoAlerta.setDescricao(regra.getDescricao() + " - Lotação atingiu " + lotacao.getTaxaOcupacao() + "%");
                
                // O timestamp já é preenchido automaticamente por padrão na tua classe, 
                // por isso não precisamos de setManual aqui.

                // 4. Grava o alerta na base de dados
                alertaRepository.save(novoAlerta);
                
                System.out.println(">>> [MOTOR] Sucesso: Alerta registado para a viatura ID: " + lotacao.getViatura().getId());
            }
        }
    }
}