package com.tub.p7_relatorios.service;

import com.tub.p7_relatorios.dto.RelatorioDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExportacaoService {

    public String gerarCSV(RelatorioDTO relatorio) {
        StringBuilder csv = new StringBuilder();

        csv.append("Titulo;").append(relatorio.getTitulo()).append("\n");
        csv.append("Tipo;").append(relatorio.getTipo()).append("\n");
        csv.append("Gerado em;").append(relatorio.getGeradoEm()).append("\n");
        csv.append("Gerado por;").append(relatorio.getGeradoPor()).append("\n\n");

        if (relatorio.getDados() == null || relatorio.getDados().isEmpty()) {
            csv.append("Sem dados disponíveis\n");
            return csv.toString();
        }

        Map<String, Object> primeiraLinha = relatorio.getDados().get(0);

        for (String coluna : primeiraLinha.keySet()) {
            csv.append(coluna).append(";");
        }

        csv.append("\n");

        for (Map<String, Object> linha : relatorio.getDados()) {
            for (Object valor : linha.values()) {
                csv.append(valor != null ? valor.toString() : "").append(";");
            }
            csv.append("\n");
        }

        return csv.toString();
    }
}