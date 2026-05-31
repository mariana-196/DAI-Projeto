package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.EventoGeografico;
import com.tub.p10_gestao_pmd.repository.EventoGeograficoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class SimuladorGeofencing {

    @Autowired
    private EventoGeograficoRepository eventoRepository;

    private final Random random = new Random();

    private final List<String> zonas = Arrays.asList(
            "Zona A - Centro Histórico",
            "Zona B - Campus Universitário",
            "Zona C - Estação CP",
            "Zona D - Hospital de Braga",
            "Zona E - Estádio Municipal"
    );

    private final List<String> movimentos = Arrays.asList("ENTRADA", "SAIDA");
    private final List<Long> viaturas = Arrays.asList(101L, 102L, 103L, 104L, 105L, 106L);

    // Executa a cada 10 segundos para gerar eventos continuamente
    @Scheduled(fixedDelay = 10000)
    public void simularEventosGeofencing() {
        Long viaturaId = viaturas.get(random.nextInt(viaturas.size()));
        String zona = zonas.get(random.nextInt(zonas.size()));
        String movimento = movimentos.get(random.nextInt(movimentos.size()));

        EventoGeografico evento = new EventoGeografico();
        evento.setViaturaId(viaturaId);
        evento.setTipo(movimento);
        evento.setDetalhes(zona);
        // O timestamp é preenchido automaticamente pelo construtor do modelo

        eventoRepository.save(evento);
        
        // Comentado para evitar poluir os logs permanentemente, mas podes descomentar para ver no terminal do VS Code
        // System.out.println("[SIMULADOR GEOFENCING] " + movimento + ": Viatura #" + viaturaId + " em " + zona);
    }
}
