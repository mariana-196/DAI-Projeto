package com.tub.p7_relatorios.service;

import com.tub.p7_relatorios.dto.RelatorioDTO;
import com.tub.p7_relatorios.dto.RelatorioFiltroDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    public RelatorioDTO gerarRelatorioAuditoria(RelatorioFiltroDTO filtro) {
        validarDatas(filtro);

        List<Map<String, Object>> dados = new ArrayList<>();

        Map<String, Object> registo1 = new LinkedHashMap<>();
        registo1.put("data", "2026-04-20 10:30");
        registo1.put("utilizador", anonimizarEmail("admin@tub.pt"));
        registo1.put("evento", "LOGIN");
        registo1.put("severidade", "INFO");
        registo1.put("resultado", "Sucesso");

        Map<String, Object> registo2 = new LinkedHashMap<>();
        registo2.put("data", "2026-04-20 11:05");
        registo2.put("utilizador", anonimizarEmail("operador@tub.pt"));
        registo2.put("evento", "TENTATIVA_FALHADA");
        registo2.put("severidade", "AVISO");
        registo2.put("resultado", "Falha");

        Map<String, Object> registo3 = new LinkedHashMap<>();
        registo3.put("data", "2026-04-20 12:15");
        registo3.put("utilizador", anonimizarEmail("admin@tub.pt"));
        registo3.put("evento", "EXPORTACAO_RELATORIO");
        registo3.put("severidade", "INFO");
        registo3.put("resultado", "Sucesso");

        dados.add(registo1);
        dados.add(registo2);
        dados.add(registo3);

        return new RelatorioDTO(
                "Relatório de Auditoria",
                "AUDITORIA",
                LocalDateTime.now(),
                "Administrador",
                dados
        );
    }

    public RelatorioDTO gerarRelatorioOperacional(RelatorioFiltroDTO filtro) {
        validarDatas(filtro);

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

        Map<String, Object> registo3 = new LinkedHashMap<>();
        registo3.put("linha", "7");
        registo3.put("veiculo", "BUS-074");
        registo3.put("paragem", "Estação");
        registo3.put("lotacaoMedia", "81%");
        registo3.put("passageirosTransportados", 1510);
        registo3.put("atrasoMedio", "6 min");

        dados.add(registo1);
        dados.add(registo2);
        dados.add(registo3);

        return new RelatorioDTO(
                "Relatório Operacional",
                "OPERACIONAL",
                LocalDateTime.now(),
                "Operador TUB",
                dados
        );
    }

    private void validarDatas(RelatorioFiltroDTO filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("Os filtros do relatório não podem estar vazios.");
        }

        if (filtro.getDataInicio() != null && filtro.getDataFim() != null) {
            if (filtro.getDataFim().isBefore(filtro.getDataInicio())) {
                throw new IllegalArgumentException("A data de fim não pode ser anterior à data de início.");
            }
        }

        if (filtro.getPeriodoComparacaoInicio() != null && filtro.getPeriodoComparacaoFim() != null) {
            if (filtro.getPeriodoComparacaoFim().isBefore(filtro.getPeriodoComparacaoInicio())) {
                throw new IllegalArgumentException("A data de fim da comparação não pode ser anterior à data de início da comparação.");
            }
        }
    }

    private String anonimizarEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "utilizador_anonimizado";
        }

        String[] partes = email.split("@");
        String nome = partes[0];
        String dominio = partes[1];

        if (nome.length() <= 1) {
            return "*@" + dominio;
        }

        return nome.charAt(0) + "***@" + dominio;
    }
}