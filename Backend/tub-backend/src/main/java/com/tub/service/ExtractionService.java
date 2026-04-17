package com.tub.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

import com.tub.model.Validation;
import com.tub.model.EstadoSincronizacao;
import com.tub.model.LoteBilhetica;

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