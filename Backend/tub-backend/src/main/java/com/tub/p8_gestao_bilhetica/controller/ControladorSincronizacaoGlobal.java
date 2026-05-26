package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.model.ConfiguracaoIntegracao;
import com.tub.p8_gestao_bilhetica.repository.ConfiguracaoIntegracaoRepository;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.service.ConnectionService;
import com.tub.p8_gestao_bilhetica.service.GestorExtracao;
import com.tub.p8_gestao_bilhetica.service.ProcessadorValidacao;
import com.tub.p8_gestao_bilhetica.service.ProcesadorArmazenamento;
import com.tub.p8_gestao_bilhetica.service.ImportadorCSV;
import com.tub.p1_autenticacao.annotation.RequerCargo;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorSincronizacaoGlobal {

    private final ConnectionService connectionService;
    private final GestorExtracao gestorExtracao;
    private final ProcessadorValidacao processadorValidacao;
    private final LoteDadosBilheticaRepository loteDadosBilheticaRepository;
    private final ProcesadorArmazenamento procesadorArmazenamento;
    private final ImportadorCSV importadorCSV;
    private final ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository;
    private final ControloConsultaAuditoria auditService;

    @Autowired
    private HttpServletRequest request;

    public ControladorSincronizacaoGlobal(
            ConnectionService connectionService,
            GestorExtracao gestorExtracao,
            ProcessadorValidacao processadorValidacao,
            LoteDadosBilheticaRepository loteDadosBilheticaRepository,
            ProcesadorArmazenamento procesadorArmazenamento,
            ImportadorCSV importadorCSV,
            ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository,
            ControloConsultaAuditoria auditService
    ) {
        this.connectionService = connectionService;
        this.gestorExtracao = gestorExtracao;
        this.processadorValidacao = processadorValidacao;
        this.loteDadosBilheticaRepository = loteDadosBilheticaRepository;
        this.procesadorArmazenamento = procesadorArmazenamento;
        this.importadorCSV = importadorCSV;
        this.configuracaoIntegracaoRepository = configuracaoIntegracaoRepository;
        this.auditService = auditService;
    }

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    @GetMapping("/configuracao")
    public ResponseEntity<?> obterConfiguracao() {
        ConfiguracaoIntegracao config = configuracaoIntegracaoRepository.findAll().stream()
                .findFirst()
                .orElse(null);
        if (config == null) {
            config = new ConfiguracaoIntegracao();
            config.setNome("Sincronização Padrão");
            config.setEndpoint("http://localhost:8081/api/bilhetica/dados");
            config.setAtiva(true);
            config.setIntervaloMinutos(2);
            configuracaoIntegracaoRepository.save(config);
        }
        return ResponseEntity.ok(config);
    }

    @PostMapping("/configuracao")
    @RequerCargo("ADMINISTRADOR")
    public ResponseEntity<?> atualizarConfiguracao(@RequestBody ConfiguracaoIntegracao novaConfig) {
        ConfiguracaoIntegracao config = configuracaoIntegracaoRepository.findAll().stream()
                .findFirst()
                .orElse(null);
        if (config == null) {
            config = new ConfiguracaoIntegracao();
            config.setNome("Sincronização Padrão");
            config.setEndpoint("http://localhost:8081/api/bilhetica/dados");
        }
        config.setAtiva(novaConfig.getAtiva());
        config.setIntervaloMinutos(novaConfig.getIntervaloMinutos());
        config.setSimulacaoMaxEntradasSaidas(novaConfig.getSimulacaoMaxEntradasSaidas());
        config.setSimulacaoMaxOcupacaoPercentual(novaConfig.getSimulacaoMaxOcupacaoPercentual());
        configuracaoIntegracaoRepository.save(config);

        try {
            auditService.registar(
                    getExecutorEmail(),
                    "ALTERAR_CONFIGURACAO",
                    "Bilhética",
                    getExecutorIp(),
                    "INFO",
                    "Configuração de sincronização e simulação de lotação alterada: ativa=" + config.getAtiva() +
                    ", intervalo=" + config.getIntervaloMinutos() + "m, maxDelta=" + config.getSimulacaoMaxEntradasSaidas() +
                    ", maxOcupacao=" + config.getSimulacaoMaxOcupacaoPercentual() + "%"
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }

        return ResponseEntity.ok(config);
    }

    @GetMapping("/sincronizar")
    @RequerCargo({"OPERADOR", "ADMINISTRADOR"})
    public ResponseEntity<?> sincronizar() {
        try {
            List<Validation> dados = connectionService.obterDadosBilhetica();
            LoteDadosBilhetica lote = procesadorArmazenamento.processarEGuardarSincronizacao(dados);
            LoteDadosBilhetica loteSimulado = procesadorArmazenamento.simularFluxosBilheticaAleatorios("SINCRONIZACAO_MANUAL");
            
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "SINCRONIZAR_BILHETICA",
                        "Bilhética",
                        getExecutorIp(),
                        "INFO",
                        "Sincronização manual de bilhética executada com sucesso. Lote ID: " + (lote != null ? lote.getId() : "N/A")
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            
            if (lote != null && loteSimulado != null) {
                return ResponseEntity.ok(List.of(lote, loteSimulado));
            }
            if (lote != null) {
                return ResponseEntity.ok(List.of(lote));
            }
            return ResponseEntity.ok(loteSimulado != null ? List.of(loteSimulado) : List.of());
        } catch (Exception e) {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "SINCRONIZAR_BILHETICA",
                        "Bilhética",
                        getExecutorIp(),
                        "ERRO",
                        "Falha na sincronização manual de bilhética: " + e.getMessage()
                );
            } catch (Exception ex) {
                System.err.println("Erro ao registar auditoria: " + ex.getMessage());
            }
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping(value = "/importar", consumes = {"multipart/form-data"})
    @RequerCargo("ADMINISTRADOR")
    public ResponseEntity<?> importarCSV(@RequestParam("ficheiro") MultipartFile ficheiro) {
        if (ficheiro == null || ficheiro.isEmpty()) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", "Nenhum ficheiro recebido.");
            return ResponseEntity.badRequest().body(erro);
        }

        try {
            int total = importadorCSV.importarFicheiro(ficheiro);
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("registosImportados", total);
            resposta.put("mensagem", "Importação concluída com sucesso!");
            
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "SINCRONIZAR_BILHETICA",
                        "Bilhética",
                        getExecutorIp(),
                        "INFO",
                        "Importação de ficheiro CSV de bilhética realizada com sucesso. Registos importados: " + total
                );
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            try {
                auditService.registar(
                        getExecutorEmail(),
                        "SINCRONIZAR_BILHETICA",
                        "Bilhética",
                        getExecutorIp(),
                        "ERRO",
                        "Falha na importação de ficheiro CSV de bilhética: " + e.getMessage()
                );
            } catch (Exception ex) {
                System.err.println("Erro ao registar auditoria: " + ex.getMessage());
            }
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", "Erro ao processar ficheiro: " + e.getMessage());
            return ResponseEntity.internalServerError().body(erro);
        }
    }
}
