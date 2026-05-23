package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tub.p8_gestao_bilhetica.model.ParametrosAnalise;
import com.tub.p8_gestao_bilhetica.model.ResultadoAnalitico;
import com.tub.p8_gestao_bilhetica.service.MotorCalculoAnalitico;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorDashboard {

    private final MotorCalculoAnalitico motorCalculoAnalitico;
    private final com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository registoRepository;

    public ControladorDashboard(
            MotorCalculoAnalitico motorCalculoAnalitico,
            com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository registoRepository
    ) {
        this.motorCalculoAnalitico = motorCalculoAnalitico;
        this.registoRepository = registoRepository;
    }

    @GetMapping("/dashboard/resultados")
    public ResultadoAnalitico obterResultados(
            @RequestParam(required = false) Long linhaId,
            @RequestParam(required = false) String periodo
    ) {
        ParametrosAnalise parametros = new ParametrosAnalise();
        parametros.setLinhaId(linhaId);
        parametros.setPeriodo(periodo);

        return motorCalculoAnalitico.calcular(parametros);
    }

    @GetMapping("/analise")
    public org.springframework.http.ResponseEntity<?> obterAnalise(@RequestParam(required = false) String linha) {
        ParametrosAnalise parametros = new ParametrosAnalise();
        if (linha != null && !linha.isEmpty() && !linha.equals("vazia")) {
            try {
                parametros.setLinhaId(Long.parseLong(linha));
            } catch (NumberFormatException e) {
                // ignora ou loga
            }
        }
        ResultadoAnalitico res = motorCalculoAnalitico.calcular(parametros);
        int total = res.getTotalPassageiros();

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("taxaOcupacaoMedia", res.getTaxaOcupacaoMedia());
        map.put("totalPassageiros", total);

        // Fetch real database records to compute the hourly distribution
        java.util.List<com.tub.p8_gestao_bilhetica.model.RegistoBilhetica> todosOsRegistos = registoRepository.findAll();
        if (linha != null && !linha.isEmpty() && !linha.equals("vazia")) {
            todosOsRegistos = todosOsRegistos.stream()
                    .filter(r -> r.getLinha() != null && 
                        (String.valueOf(r.getLinha().getId()).equals(linha) || 
                         r.getLinha().getCodigo().equals(linha)))
                    .toList();
        }

        int slot0 = 0; // 08h-10h (or earlier)
        int slot1 = 0; // 10h-12h
        int slot2 = 0; // 12h-14h
        int slot3 = 0; // 14h-16h
        int slot4 = 0; // 16h-18h (or later)

        for (com.tub.p8_gestao_bilhetica.model.RegistoBilhetica r : todosOsRegistos) {
            int hour = r.getDataHora().getHour();
            int vals = r.getValidacoes() != null ? r.getValidacoes() : 0;
            if (hour < 10) {
                slot0 += vals;
            } else if (hour < 12) {
                slot1 += vals;
            } else if (hour < 14) {
                slot2 += vals;
            } else if (hour < 16) {
                slot3 += vals;
            } else {
                slot4 += vals;
            }
        }

        java.util.List<Integer> procura = java.util.Arrays.asList(slot0, slot1, slot2, slot3, slot4);
        map.put("procura", procura);

        return org.springframework.http.ResponseEntity.ok(map);
    }
}