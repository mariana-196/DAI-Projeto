package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.repository.LoteBilheticaRepository;
import com.tub.p8_gestao_bilhetica.service.ProcessadorValidacao;
import com.tub.p8_gestao_bilhetica.service.ConnectionService;
import com.tub.p8_gestao_bilhetica.service.GestorExtracao;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorSincronizacaoGlobal {

    private final ConnectionService connectionService;
    private final GestorExtracao extractionService;
    private final ProcessadorValidacao validationService;
    private final LoteBilheticaRepository repository;

    public ControladorSincronizacaoGlobal(
            ConnectionService connectionService,
            GestorExtracao extractionService,
            ProcessadorValidacao validationService,
            LoteBilheticaRepository repository
    ) {
        this.connectionService = connectionService;
        this.extractionService = extractionService;
        this.validationService = validationService;
        this.repository = repository;
    }

    @GetMapping("/sincronizar")
    public ResponseEntity<?> sincronizar() {

        List<Validation> dados = connectionService.obterDadosBilhetica();
        List<LoteDadosBilhetica> lotes = extractionService.extrair(dados);
        List<LoteDadosBilhetica> validos = validationService.validar(lotes);

        repository.saveAll(validos);
                return ResponseEntity.ok(validos);
    }
}