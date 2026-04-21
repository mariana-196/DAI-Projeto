package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;

import com.tub.p8_gestao_bilhetica.model.LoteBilhetica;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BilheticaValidationService {

    public List<LoteBilhetica> validar(List<LoteBilhetica> lotes) {
        return lotes.stream()
                .filter(l -> l.getCodigoLote() != null)
                .collect(Collectors.toList());
    }
}
