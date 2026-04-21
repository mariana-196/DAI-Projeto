package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.tub.p3_integracao_externa.adapter.BilheticaAdapter;
import com.tub.p3_integracao_externa.model.Validation;

@Service
public class ConnectionService {

    private final BilheticaAdapter bilheticaAdapter;

    public ConnectionService(BilheticaAdapter bilheticaAdapter) {
        this.bilheticaAdapter = bilheticaAdapter;
    }

    public List<Validation> obterDadosBilhetica() {
        return bilheticaAdapter.getValidations();
    }
}
