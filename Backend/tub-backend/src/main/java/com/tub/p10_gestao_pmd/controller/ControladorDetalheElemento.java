package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.service.MapaOperacaoService; // 1. Importar o SERVIÇO correto
import com.tub.p8_gestao_bilhetica.model.ContextoElementoMapa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mapa")
@CrossOrigin(origins = "*")
public class ControladorDetalheElemento {

    @Autowired
    private MapaOperacaoService mapaOperacaoService; // 2. Mudar de Controlador para MapaOperacaoService

    @GetMapping("/elemento/{id}")
    public ContextoElementoMapa getDetalhesElemento(@PathVariable String id) {
        // 3. Chamar o método na variável do serviço
        return mapaOperacaoService.obterContextoViatura(id); 
    }
}