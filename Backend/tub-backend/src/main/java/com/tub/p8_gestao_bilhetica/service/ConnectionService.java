package com.tub.p8_gestao_bilhetica.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.tub.p3_integracao_externa.adapter.GatewayIntegracaoBilhetica;
import com.tub.p3_integracao_externa.model.Validation;

@Service
public class ConnectionService {

    private final GatewayIntegracaoBilhetica bilheticaAdapter;

    public ConnectionService(GatewayIntegracaoBilhetica bilheticaAdapter) {
        this.bilheticaAdapter = bilheticaAdapter;
    }

    public List<Validation> obterDadosBilhetica() {
        return bilheticaAdapter.getValidations();
    }
}
