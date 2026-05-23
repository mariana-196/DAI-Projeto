package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.model.ConfiguracaoIntegracao;
import com.tub.p8_gestao_bilhetica.repository.ConfiguracaoIntegracaoRepository;
import com.tub.p8_gestao_bilhetica.service.ConnectionService;
import com.tub.p8_gestao_bilhetica.service.ProcesadorArmazenamento;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorSincronizacaoGlobal {

    private final ConnectionService connectionService;
    private final ProcesadorArmazenamento procesadorArmazenamento;
    private final ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository;

    public ControladorSincronizacaoGlobal(
            ConnectionService connectionService,
            ProcesadorArmazenamento procesadorArmazenamento,
            ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository
    ) {
        this.connectionService = connectionService;
        this.procesadorArmazenamento = procesadorArmazenamento;
        this.configuracaoIntegracaoRepository = configuracaoIntegracaoRepository;
    }

    @GetMapping("/sincronizar")
    public ResponseEntity<?> sincronizar() {
        List<Validation> dados = connectionService.obterDadosBilhetica();
        LoteDadosBilhetica lote = procesadorArmazenamento.processarEGuardarSincronizacao(dados);
        return ResponseEntity.ok(lote != null ? List.of(lote) : List.of());
    }

    @PostMapping("/importar")
    public ResponseEntity<?> importar(@RequestParam(required = false) String periodo) {
        List<Validation> dados = connectionService.obterDadosBilhetica();
        LoteDadosBilhetica lote = procesadorArmazenamento.processarEGuardarSincronizacao(dados);
        return ResponseEntity.ok(lote != null ? List.of(lote) : List.of());
    }

    @GetMapping("/configuracao")
    public ResponseEntity<?> obterConfiguracao() {
        ConfiguracaoIntegracao config = configuracaoIntegracaoRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    ConfiguracaoIntegracao defaultCc = new ConfiguracaoIntegracao();
                    defaultCc.setNome("Sincronizacao Validadores");
                    defaultCc.setEndpoint("http://api.tub.pt/validadores");
                    defaultCc.setToken("default_token");
                    defaultCc.setAtiva(true);
                    defaultCc.setIntervaloMinutos(2);
                    defaultCc.setSimulacaoMaxEntradasSaidas(10);
                    defaultCc.setSimulacaoMaxOcupacaoPercentual(90);
                    return configuracaoIntegracaoRepository.save(defaultCc);
                });
        return ResponseEntity.ok(config);
    }

    @PostMapping("/configuracao")
    public ResponseEntity<?> atualizarConfiguracao(@RequestBody ConfiguracaoIntegracao novaConfig) {
        ConfiguracaoIntegracao config = configuracaoIntegracaoRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    ConfiguracaoIntegracao defaultCc = new ConfiguracaoIntegracao();
                    defaultCc.setNome("Sincronizacao Validadores");
                    defaultCc.setEndpoint("http://api.tub.pt/validadores");
                    defaultCc.setToken("default_token");
                    defaultCc.setAtiva(true);
                    defaultCc.setIntervaloMinutos(2);
                    defaultCc.setSimulacaoMaxEntradasSaidas(10);
                    defaultCc.setSimulacaoMaxOcupacaoPercentual(90);
                    return defaultCc;
                });
        
        if (novaConfig.getIntervaloMinutos() != null) {
            config.setIntervaloMinutos(novaConfig.getIntervaloMinutos());
        }
        if (novaConfig.getAtiva() != null) {
            config.setAtiva(novaConfig.getAtiva());
        }
        if (novaConfig.getEndpoint() != null) {
            config.setEndpoint(novaConfig.getEndpoint());
        }
        if (novaConfig.getToken() != null) {
            config.setToken(novaConfig.getToken());
        }
        if (novaConfig.getSimulacaoMaxEntradasSaidas() != null) {
            config.setSimulacaoMaxEntradasSaidas(novaConfig.getSimulacaoMaxEntradasSaidas());
        }
        if (novaConfig.getSimulacaoMaxOcupacaoPercentual() != null) {
            config.setSimulacaoMaxOcupacaoPercentual(novaConfig.getSimulacaoMaxOcupacaoPercentual());
        }
        
        configuracaoIntegracaoRepository.save(config);
        return ResponseEntity.ok(config);
    }
}