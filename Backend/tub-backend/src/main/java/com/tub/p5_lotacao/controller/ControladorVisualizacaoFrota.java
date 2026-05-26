package com.tub.p5_lotacao.controller;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tub.p9_monitorizacao_iot.controller.ControladorMonitorizacaoLotacao;
import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;

// Classe wrapper para respeitar a arquitetura
@RestController
@RequestMapping("/api/frota")
@CrossOrigin(origins = "*")
public class ControladorVisualizacaoFrota {

    private final ControladorMonitorizacaoLotacao monitorizacaoLotacao;

    // Injeção via construtor
    public ControladorVisualizacaoFrota(ControladorMonitorizacaoLotacao monitorizacaoLotacao) {
        this.monitorizacaoLotacao = monitorizacaoLotacao;
    }

    // Endpoint para obter o estado de ocupação da frota
    @GetMapping("/status")
    public List<EstadoOcupacaoViatura> obterStatusFrota() {
        try {
            Method method = obterMetodoEstadoOcupacao();
            @SuppressWarnings("unchecked")
            List<EstadoOcupacaoViatura> status = (List<EstadoOcupacaoViatura>) method.invoke(monitorizacaoLotacao);
            return status;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao obter o estado de ocupação da frota", e);
        }
    }

    private Method obterMetodoEstadoOcupacao() throws NoSuchMethodException {
        try {
            return ControladorMonitorizacaoLotacao.class.getMethod("obterEstadoOcupacao");
        } catch (NoSuchMethodException ignored) {
            for (Method method : monitorizacaoLotacao.getClass().getMethods()) {
                if (method.getParameterCount() == 0
                        && List.class.isAssignableFrom(method.getReturnType())
                        && method.getName().toLowerCase().contains("ocupacao")) {
                    return method;
                }
            }
            throw new NoSuchMethodException("Nenhum método de estado de ocupação encontrado em "
                    + monitorizacaoLotacao.getClass().getName());
        }
    }
}