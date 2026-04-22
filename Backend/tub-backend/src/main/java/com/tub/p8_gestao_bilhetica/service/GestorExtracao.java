package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.EstadoSincronizacao;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;

@Service
public class GestorExtracao {

    public List<LoteDadosBilhetica> extrair(List<Validation> validations) {
        List<LoteDadosBilhetica> lotes = new ArrayList<>();

        for (Validation v : validations) {
            LoteDadosBilhetica lote = new LoteDadosBilhetica();
            lote.setCodigoLote("LOTE_" + System.currentTimeMillis());
            lote.setOrigem("BILHETICA");
            lote.setEstado(EstadoSincronizacao.RECEBIDO);
            lotes.add(lote);
        }

        return lotes;
    }
}