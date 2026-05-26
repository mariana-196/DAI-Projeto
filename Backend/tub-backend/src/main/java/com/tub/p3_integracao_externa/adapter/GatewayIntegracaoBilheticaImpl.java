package com.tub.p3_integracao_externa.adapter;

import org.springframework.stereotype.Component;
import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
        List<Viatura> viaturas = viaturasRepository.findAll().stream()
                .filter(Viatura::isAtiva)
                .toList();

        if (linhas.isEmpty() || viaturas.isEmpty()) {
            return list;
        }

        String[] ticketTypes = {"Passe Estudante", "Bilhete Normal", "Passe Sénior", "Passe Turístico"};
        String[] paragens = {
            "Universidade do Minho", "Estação CP", "Hospital de Braga", 
            "Avenida Central", "Bom Jesus", "Arcada", "Gualtar", "Braga Parque", "Lamaçães"
        };

        int count = Math.min(6, viaturas.size());
        for (int i = 0; i < count; i++) {
            Linha linha = linhas.get(random.nextInt(linhas.size()));
            Viatura viatura = viaturas.get(i);
            String ticketType = ticketTypes[random.nextInt(ticketTypes.length)];
            String stop = paragens[random.nextInt(paragens.length)];
            LocalDateTime timestamp = gerarInstanteServico();

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

    private LocalDateTime gerarInstanteServico() {
        LocalDateTime agora = LocalDateTime.now();
        LocalTime horaAtual = agora.toLocalTime();
        LocalTime inicioServico = LocalTime.of(6, 20);
        LocalTime fimServico = LocalTime.of(1, 30);

        if (!horaAtual.isBefore(inicioServico) || horaAtual.isBefore(fimServico)) {
            return agora;
        }

        return agora.withHour(6).withMinute(20).withSecond(0).withNano(0)
                .plusMinutes(random.nextInt(30));
    }
}
