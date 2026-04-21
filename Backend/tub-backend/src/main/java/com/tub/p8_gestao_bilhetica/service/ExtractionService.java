package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.EstadoSincronizacao;
import com.tub.p8_gestao_bilhetica.model.LoteBilhetica;

@Service
public class ExtractionService {

    public List<LoteBilhetica> extrair(List<Validation> validations) {
        List<LoteBilhetica> lotes = new ArrayList<>();

        for (Validation v : validations) {
            LoteBilhetica lote = new LoteBilhetica();
            lote.setCodigoLote("LOTE_" + System.currentTimeMillis());
            lote.setOrigem("BILHETICA");
            lote.setEstado(EstadoSincronizacao.RECEBIDO);
            lotes.add(lote);
        }

        return lotes;
    }
}