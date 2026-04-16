package com.tub.adapter;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import com.tub.model.Validation;

@Component
public class BilheticaAdapterImpl implements BilheticaAdapter {

    @Override
    public List<Validation> getValidations() {
        // MOCK por agora
        return new ArrayList<>();
    }
}
