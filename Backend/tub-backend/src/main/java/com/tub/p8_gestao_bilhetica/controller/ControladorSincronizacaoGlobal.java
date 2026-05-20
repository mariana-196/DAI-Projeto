package com.tub.p8_gestao_bilhetica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.tub.p3_integracao_externa.model.Validation;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.service.ConnectionService;
import com.tub.p8_gestao_bilhetica.service.GestorExtracao;
import com.tub.p8_gestao_bilhetica.service.ProcessadorValidacao;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*")
public class ControladorSincronizacaoGlobal {

    private final ConnectionService connectionService;
    private final GestorExtracao gestorExtracao;
    private final ProcessadorValidacao processadorValidacao;
    private final LoteDadosBilheticaRepository loteDadosBilheticaRepository;

    public ControladorSincronizacaoGlobal(
            ConnectionService connectionService,
            GestorExtracao gestorExtracao,
            ProcessadorValidacao processadorValidacao,
            LoteDadosBilheticaRepository loteDadosBilheticaRepository
    ) {
        this.connectionService = connectionService;
        this.gestorExtracao = gestorExtracao;
        this.processadorValidacao = processadorValidacao;
        this.loteDadosBilheticaRepository = loteDadosBilheticaRepository;
    }

    @GetMapping("/sincronizar")
    public ResponseEntity<?> sincronizar() {
        List<Validation> dados = connectionService.obterDadosBilhetica();
        List<LoteDadosBilhetica> lotes = gestorExtracao.extrair(dados);
        List<LoteDadosBilhetica> validos = processadorValidacao.validar(lotes);

        loteDadosBilheticaRepository.saveAll(validos);

        return ResponseEntity.ok(validos);
    }

    @PostMapping("/importar")
    public ResponseEntity<?> importar(@RequestParam(required = false) String periodo) {
        List<Validation> dados = connectionService.obterDadosBilhetica();
        List<LoteDadosBilhetica> lotes = gestorExtracao.extrair(dados);
        List<LoteDadosBilhetica> validos = processadorValidacao.validar(lotes);

        loteDadosBilheticaRepository.saveAll(validos);

        return ResponseEntity.ok(validos);
    }
}