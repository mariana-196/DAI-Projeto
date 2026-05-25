package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    public ControladorSincronizacaoGlobal(
            ConnectionService connectionService,
            GestorExtracao gestorExtracao,
            ProcessadorValidacao processadorValidacao,
            LoteDadosBilheticaRepository loteDadosBilheticaRepository,
            ProcesadorArmazenamento procesadorArmazenamento,
            ImportadorCSV importadorCSV
    ) {
        this.connectionService = connectionService;
        this.gestorExtracao = gestorExtracao;
        this.processadorValidacao = processadorValidacao;
        this.loteDadosBilheticaRepository = loteDadosBilheticaRepository;
        this.procesadorArmazenamento = procesadorArmazenamento;
        this.importadorCSV = importadorCSV;
    }

    @GetMapping("/sincronizar")
    public ResponseEntity<?> sincronizar() {
        List<Validation> dados = connectionService.obterDadosBilhetica();
        LoteDadosBilhetica lote = procesadorArmazenamento.processarEGuardarSincronizacao(dados);
        return ResponseEntity.ok(lote != null ? List.of(lote) : List.of());
    }

    @PostMapping(value = "/importar", consumes = {"multipart/form-data"})
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
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", "Erro ao processar ficheiro: " + e.getMessage());
            return ResponseEntity.internalServerError().body(erro);
        }
    }
}