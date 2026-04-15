package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import com.tub.service.ConnectionService;
import com.tub.service.ExtractionService;
import com.tub.service.BilheticaValidationService;
import com.tub.repository.LoteBilheticaRepository;
import com.tub.model.Validation;
import com.tub.model.LoteBilhetica;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class BilheticaController {

    private final ConnectionService connectionService;
    private final ExtractionService extractionService;
    private final BilheticaValidationService validationService;
    private final LoteBilheticaRepository repository;

    public BilheticaController(
            ConnectionService connectionService,
            ExtractionService extractionService,
            BilheticaValidationService validationService,
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
        List<LoteBilhetica> lotes = extractionService.extrair(dados);
        List<LoteBilhetica> validos = validationService.validar(lotes);

        repository.saveAll(validos);
                return ResponseEntity.ok(validos);
    }
}