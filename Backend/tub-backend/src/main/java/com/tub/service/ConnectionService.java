package com.tub.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.tub.adapter.BilheticaAdapter;
import com.tub.model.Validation;

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
