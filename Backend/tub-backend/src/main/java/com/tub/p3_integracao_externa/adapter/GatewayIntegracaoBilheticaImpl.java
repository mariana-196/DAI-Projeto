package com.tub.p3_integracao_externa.adapter;

import org.springframework.stereotype.Component;

import com.tub.p3_integracao_externa.model.Validation;

import java.util.List;
import java.util.ArrayList;

@Component
public class GatewayIntegracaoBilheticaImpl implements GatewayIntegracaoBilhetica {

    @Override
    public List<Validation> getValidations() {
        // MOCK por agora
        return new ArrayList<>();
    }
}
