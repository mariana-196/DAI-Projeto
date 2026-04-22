package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.PrevisaoChegada;
import com.tub.p10_gestao_pmd.model.PainelPMD;

// AQUI ESTÃO OS TEUS IMPORTS CORRIGIDOS:
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;

import com.tub.p10_gestao_pmd.repository.PrevisaoChegadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrevisaoService {

    @Autowired
    private PrevisaoChegadaRepository previsaoRepository;

    private static final double TEMPO_MEDIO_PARAGEM = 2.5;

    public PrevisaoChegada calcularEGuardarPrevisao(Long viaturaId, Long painelId, Long linhaId, String destino, int paragensRestantes) {
        
        int tempoFinal = 0;
        if (paragensRestantes > 0) {
            tempoFinal = (int) Math.round(paragensRestantes * TEMPO_MEDIO_PARAGEM);
        }

        // Criar as ligações usando os imports corretos
        Viatura viatura = new Viatura();
        viatura.setId(viaturaId);
        
        PainelPMD painel = new PainelPMD();
        painel.setId(painelId);

        Linha linha = new Linha();
        linha.setId(linhaId);

        PrevisaoChegada previsao = new PrevisaoChegada();
        previsao.setViatura(viatura);
        previsao.setPainel(painel);
        previsao.setLinha(linha);
        previsao.setDestino(destino);
        previsao.setEtaMinutos(tempoFinal); 

        return previsaoRepository.save(previsao);
    }

    public List<PrevisaoChegada> obterPrevisoesDaParagem(Long painelId) {
        return previsaoRepository.findAll().stream()
                .filter(p -> p.getPainel() != null && p.getPainel().getId().equals(painelId))
                .collect(Collectors.toList());
    }

    public boolean isStatusOk() {
        return true;
    }
}