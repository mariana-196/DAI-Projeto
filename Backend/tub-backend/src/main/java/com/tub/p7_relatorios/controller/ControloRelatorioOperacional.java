package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.DadosRelatorio;
import com.tub.p7_relatorios.service.MotorGrafico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios/operacional")
@CrossOrigin(origins = "*")
public class ControloRelatorioOperacional {

    private final MotorGrafico motorGrafico;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ControloConsultaAuditoria auditService;

    public ControloRelatorioOperacional(MotorGrafico motorGrafico) {
        this.motorGrafico = motorGrafico;
    }

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    @PostMapping
    public ResponseEntity<DadosRelatorio> gerarRelatorioOperacional(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String linha,
            @RequestParam(required = false) String veiculo,
            @RequestParam(required = false) String paragem
    ) {
        List<Map<String, Object>> dados = new ArrayList<>();

        Map<String, Object> registo1 = new LinkedHashMap<>();
        registo1.put("linha", "43");
        registo1.put("veiculo", "BUS-102");
        registo1.put("paragem", "Universidade do Minho");
        registo1.put("lotacaoMedia", "72%");
        registo1.put("passageirosTransportados", 1240);
        registo1.put("atrasoMedio", "4 min");

        Map<String, Object> registo2 = new LinkedHashMap<>();
        registo2.put("linha", "12");
        registo2.put("veiculo", "BUS-088");
        registo2.put("paragem", "Avenida Central");
        registo2.put("lotacaoMedia", "55%");
        registo2.put("passageirosTransportados", 890);
        registo2.put("atrasoMedio", "2 min");

        Map<String, Object> grafico = motorGrafico.gerarResumoGrafico(2130, "64%");

        dados.add(registo1);
        dados.add(registo2);
        dados.add(grafico);

        DadosRelatorio relatorio = new DadosRelatorio(
                "Relatório Operacional",
                "OPERACIONAL",
                LocalDateTime.now(),
                "Operador TUB",
                dados
        );

        auditService.registar(
                getExecutorEmail(),
                "CONSULTAR_RELATORIO",
                "Relatórios",
                getExecutorIp(),
                "INFO",
                "Relatório operacional consultado. Linha: " + linha + ", Veículo: " + veiculo + ", Paragem: " + paragem
        );

        return ResponseEntity.ok(relatorio);
    }
}