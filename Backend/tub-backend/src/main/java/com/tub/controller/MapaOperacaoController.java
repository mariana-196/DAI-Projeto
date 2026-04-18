package com.tub.controller;

import com.tub.model.ContextoElementoMapa;
import com.tub.service.MapaOperacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mapa")
@CrossOrigin(origins = "*")
public class MapaOperacaoController {

    @Autowired
    private MapaOperacaoService mapaOperacaoService;

    /**
     * Linha 67: ControladorDetalheElemento
     * Endpoint para recolher e devolver o contexto geográfico e operacional de um elemento.
     */
    @GetMapping("/elemento/{id}")
    public ContextoElementoMapa getDetalhesElemento(@PathVariable String id) {
        return mapaOperacaoService.obterContextoViatura(id);
    }
}
