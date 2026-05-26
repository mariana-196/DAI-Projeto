package com.tub.p9_monitorizacao_iot.controller;

import com.tub.p3_integracao_externa.adapter.InterfaceTelemetriaLotacao;
import com.tub.p3_integracao_externa.model.PassengerCount;
import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;
import com.tub.p9_monitorizacao_iot.repository.LotacaoViaturaRepository;
import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p11_gestao_alertas.model.AlertaOperacional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@RestController("controladorMonitorizacaoLotacaoController")
@RequestMapping("/api/monitorizacao")
@CrossOrigin(origins = "*")
public class ControladorMonitorizacaoLotacao {

    @Autowired
    private InterfaceTelemetriaLotacao wavecomAdapter;

    @Autowired
    private com.tub.p9_monitorizacao_iot.service.SeviceMonitorizacaoLotacao contagemService;

    @Autowired
    private LotacaoViaturaRepository lotacaoViaturaRepository;

    @Autowired
    private com.tub.p8_gestao_bilhetica.service.ProcesadorArmazenamento procesadorArmazenamento;

    @Autowired
    private com.tub.p11_gestao_alertas.repository.AlertaOperacionalRepository alertaOperacionalRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ControloConsultaAuditoria auditService;

    private int passageirosAtual = 10;
    private boolean sinalAtivo = true;
    private final int CAPACIDADE_MAXIMA = 50;

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Dispositivo/Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    @GetMapping("/sincronizar")
    public ResponseEntity<List<PassengerCount>> sincronizarSensores() {
        List<PassengerCount> contagens = wavecomAdapter.getPassengerCounts();

        contagemService.processarContagens(contagens);

        // Run the dynamic database-backed sensor simulation
        try {
            procesadorArmazenamento.simularSensoresLotacao();
            procesadorArmazenamento.simularFluxosBilheticaAleatorios("SINCRONIZACAO_LOTACAO_IOT");
        } catch (Exception e) {
            System.err.println("Erro ao executar simulação de sensores de lotação: " + e.getMessage());
        }

        for (PassengerCount c : contagens) {
            this.passageirosAtual += (c.getPassengersIn() - c.getPassengersOut());
        }

        if (this.passageirosAtual < 0) {
            this.passageirosAtual = 0;
        }

        try {
            auditService.registar(
                    getExecutorEmail(),
                    "SINCRONIZAR_SENSORES",
                    "Lotação",
                    getExecutorIp(),
                    "INFO",
                    "Sincronização manual dos sensores de lotação acionada."
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }

        return ResponseEntity.ok(contagens);
    }

    @PostMapping("/sensor-movel")
    public ResponseEntity<?> receberDadosSensorMovel(@RequestBody Map<String, Object> payload) {
        try {
            Object viaturaIdObj = payload.get("viaturaId");
            Object passageirosObj = payload.get("passageiros");

            if (viaturaIdObj == null || passageirosObj == null) {
                return ResponseEntity.badRequest().body("Campos viaturaId e passageiros são obrigatórios.");
            }

            Integer viaturaCodigo;
            if (viaturaIdObj instanceof Number) {
                viaturaCodigo = ((Number) viaturaIdObj).intValue();
            } else {
                viaturaCodigo = Integer.parseInt(viaturaIdObj.toString());
            }

            Integer passageiros;
            if (passageirosObj instanceof Number) {
                passageiros = ((Number) passageirosObj).intValue();
            } else {
                passageiros = Integer.parseInt(passageirosObj.toString());
            }

            Optional<EstadoOcupacaoViatura> estadoOpt = lotacaoViaturaRepository.findAll().stream()
                    .filter(e -> e.getViatura() != null && e.getViatura().getCodigo().equals(viaturaCodigo))
                    .findFirst();

            if (estadoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            EstadoOcupacaoViatura estado = estadoOpt.get();
            Viatura v = estado.getViatura();
            int capMax = v.getCapacidadeMaxima() != null ? v.getCapacidadeMaxima() : 80;

            if (passageiros < 0) {
                passageiros = 0;
            }
            if (passageiros > capMax) {
                passageiros = capMax;
            }

            double taxa = ((double) passageiros / capMax) * 100;

            estado.setPassageirosAtuais(passageiros);
            estado.setTaxaOcupacao(taxa);
            estado.setUltimaAtualizacao(java.time.LocalDateTime.now());
            lotacaoViaturaRepository.save(estado);

            // Handle AlertaLotacao for critical occupancies (>= 70%)
            if (taxa >= 70.0) {
                boolean alertaExiste = alertaOperacionalRepository.findAll().stream()
                        .anyMatch(a -> a.getViatura() != null && 
                                       a.getViatura().getId().equals(v.getId()) && 
                                       a.getEstado() != null && 
                                       !a.getEstado().equalsIgnoreCase("RESOLVIDO") &&
                                       "LOTACAO".equals(a.getTema()));

                if (!alertaExiste) {
                    AlertaOperacional novoAlerta = new AlertaOperacional(
                            v,
                            estado.getLinha(),
                            "Lotação Crítica (Sensor Móvel)",
                            "LOTACAO",
                            "CRITICO",
                            "PENDENTE",
                            "Lotação Crítica - Viatura #" + viaturaCodigo + " (Sensor Móvel) atingiu " + String.format("%.1f", taxa) + "% na " + estado.getLinha(),
                            "Wavecom IoT",
                            "Sensor Móvel - Taxa: " + String.format("%.1f", taxa) + "%. Passageiros: " + passageiros + "/" + capMax
                    );
                    alertaOperacionalRepository.save(novoAlerta);
                }
            }

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("status", "Sucesso");
            resposta.put("viaturaId", viaturaCodigo);
            resposta.put("passageiros", passageiros);
            resposta.put("taxaOcupacao", taxa);

            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_SENSOR_MOVEL",
                        "Lotação",
                        getExecutorIp(),
                        "INFO",
                        "Leitura de sensor móvel atualizada para viatura #" + viaturaCodigo + " (passageiros: " + passageiros + ", taxa: " + String.format("%.1f", taxa) + "%)."
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "ATUALIZAR_SENSOR_MOVEL",
                        "Lotação",
                        getExecutorIp(),
                        "ERRO",
                        "Falha ao processar dados de telemetria móvel: " + e.getMessage()
                );
            } catch (Exception ex) {
                System.err.println("Erro ao registar auditoria: " + ex.getMessage());
            }
            return ResponseEntity.internalServerError().body("Erro ao processar dados de telemetria móvel: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("passageiros", this.passageirosAtual);
        status.put("sinal", this.sinalAtivo);
        status.put("capacidade", this.CAPACIDADE_MAXIMA);
        status.put("taxaOcupacao", (double) this.passageirosAtual / CAPACIDADE_MAXIMA * 100);
        return status;
    }

    @GetMapping("/status-geral")
    public ResponseEntity<List<Map<String, Object>>> getStatusGeral() {
        List<EstadoOcupacaoViatura> estados = lotacaoViaturaRepository.findAll();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (EstadoOcupacaoViatura estado : estados) {
            Map<String, Object> item = new HashMap<>();
            Viatura v = estado.getViatura();

            item.put("id", v != null ? v.getCodigo() : estado.getId());
            item.put("linha", estado.getLinha());
            item.put("passageiros", estado.getPassageirosAtuais());
            item.put("capacidade", v != null ? v.getCapacidadeMaxima() : CAPACIDADE_MAXIMA);
            item.put("sinal", estado.isSinalAtivo());

            resposta.add(item);
        }

        return ResponseEntity.ok(resposta);
    }
}
