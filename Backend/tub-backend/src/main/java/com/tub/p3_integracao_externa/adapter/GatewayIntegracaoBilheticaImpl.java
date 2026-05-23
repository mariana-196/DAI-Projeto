package com.tub.p3_integracao_externa.adapter;

import org.springframework.stereotype.Component;
import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class GatewayIntegracaoBilheticaImpl implements GatewayIntegracaoBilhetica {

    private final LinhaRepository linhaRepository;
    private final ViaturasRepository viaturasRepository;
    private final Random random = new Random();

    public GatewayIntegracaoBilheticaImpl(LinhaRepository linhaRepository, ViaturasRepository viaturasRepository) {
        this.linhaRepository = linhaRepository;
        this.viaturasRepository = viaturasRepository;
    }

    @Override
    public List<Validation> getValidations() {
        List<Validation> list = new ArrayList<>();
        List<Linha> linhas = linhaRepository.findAll();
        List<Viatura> viaturas = viaturasRepository.findAll();

        if (linhas.isEmpty() || viaturas.isEmpty()) {
            return list;
        }

        String[] ticketTypes = {"Passe Estudante", "Bilhete Normal", "Passe Sénior", "Passe Turístico"};
        String[] paragens = {
            "Universidade do Minho", "Estação CP", "Hospital de Braga", 
            "Avenida Central", "Bom Jesus", "Arcada", "Gualtar", "Braga Parque", "Lamaçães"
        };

        // Generate between 15 and 35 validations
        int count = 15 + random.nextInt(21);
        for (int i = 0; i < count; i++) {
            Linha linha = linhas.get(random.nextInt(linhas.size()));
            Viatura viatura = viaturas.get(random.nextInt(viaturas.size()));
            String ticketType = ticketTypes[random.nextInt(ticketTypes.length)];
            String stop = paragens[random.nextInt(paragens.length)];
            
            // Random timestamp in the last 4 hours
            LocalDateTime timestamp = LocalDateTime.now().minusMinutes(random.nextInt(240));

            Validation v = new Validation();
            v.setValidatorId("VAL_" + (100 + random.nextInt(900)));
            v.setLineId(linha.getCodigo());
            v.setVehicleId(viatura.getCodigo().toString());
            v.setStopId(stop);
            v.setTimestamp(timestamp);
            v.setTicketType(ticketType);

            list.add(v);
        }

        return list;
    }
}
