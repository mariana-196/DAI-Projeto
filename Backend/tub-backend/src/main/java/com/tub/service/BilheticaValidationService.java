package com.tub.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.tub.model.LoteBilhetica;

@Service
public class BilheticaValidationService {

    public List<LoteBilhetica> validar(List<LoteBilhetica> lotes) {
        return lotes.stream()
                .filter(l -> l.getCodigoLote() != null)
                .collect(Collectors.toList());
    }
}
