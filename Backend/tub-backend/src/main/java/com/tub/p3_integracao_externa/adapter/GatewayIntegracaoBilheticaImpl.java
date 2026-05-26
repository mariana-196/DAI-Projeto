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
    private static final LocalTime[] HORAS_SERVICO_DEMO = {
            LocalTime.of(6, 35), LocalTime.of(8, 40), LocalTime.of(10, 30),
            LocalTime.of(12, 25), LocalTime.of(14, 35), LocalTime.of(16, 45),
            LocalTime.of(18, 30), LocalTime.of(20, 35), LocalTime.of(22, 40),
            LocalTime.of(0, 45)
    };

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

        int autocarrosConsiderados = Math.min(6, viaturas.size());
        int count = Math.min(HORAS_SERVICO_DEMO.length, autocarrosConsiderados * 2);
        for (int i = 0; i < count; i++) {
            Linha linha = linhas.get((i + random.nextInt(linhas.size())) % linhas.size());
            Viatura viatura = viaturas.get(i % autocarrosConsiderados);
            String ticketType = ticketTypes[random.nextInt(ticketTypes.length)];
            String stop = paragens[random.nextInt(paragens.length)];
            LocalDateTime timestamp = gerarInstanteServico(i);

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

    private LocalDateTime gerarInstanteServico(int indiceIntervalo) {
        LocalDateTime agora = LocalDateTime.now();
        LocalTime horaBase = HORAS_SERVICO_DEMO[indiceIntervalo % HORAS_SERVICO_DEMO.length]
                .plusMinutes(random.nextInt(35));
        if (horaBase.isBefore(LocalTime.of(1, 31))) {
            return agora.plusDays(1).withHour(horaBase.getHour()).withMinute(horaBase.getMinute()).withSecond(0).withNano(0);
        }
        return agora.withHour(horaBase.getHour()).withMinute(horaBase.getMinute()).withSecond(0).withNano(0);
    }
}
