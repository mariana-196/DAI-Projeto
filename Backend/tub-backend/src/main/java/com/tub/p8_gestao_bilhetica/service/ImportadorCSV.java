package com.tub.p8_gestao_bilhetica.service;

import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p8_gestao_bilhetica.model.EstadoSincronizacao;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImportadorCSV {

    private final RegistoBilheticaRepository registoRepository;
    private final LoteDadosBilheticaRepository loteRepository;
    private final LinhaRepository linhaRepository;

    public ImportadorCSV(RegistoBilheticaRepository registoRepository,
                         LoteDadosBilheticaRepository loteRepository,
                         LinhaRepository linhaRepository) {
        this.registoRepository = registoRepository;
        this.loteRepository = loteRepository;
        this.linhaRepository = linhaRepository;
    }

    public int importarFicheiro(MultipartFile ficheiro) throws Exception {
        // Criar um lote único para esta importação
        LoteDadosBilhetica lote = new LoteDadosBilhetica();
        lote.setCodigoLote("UPLOAD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        lote.setOrigem("CSV_UPLOAD");
        lote.setEstado(EstadoSincronizacao.PROCESSADO);
        loteRepository.save(lote);

        // Obter ou criar uma linha por defeito
        Linha linha = linhaRepository.findAll().stream().findFirst().orElseGet(() -> {
            Linha l = new Linha();
            l.setCodigo("L1");
            l.setNome("Linha Principal Braga");
            l.setOrigem("Braga Parque");
            l.setDestino("Estação CP");
            l.setAtiva(true);
            return linhaRepository.save(l);
        });

        List<RegistoBilhetica> registos = new ArrayList<>();
        int linhasProcessadas = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(ficheiro.getInputStream(), "UTF-8"))) {
            String linha_csv;
            boolean isFirstLine = true;
            while ((linha_csv = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // saltar cabeçalho
                }
                String[] data = linha_csv.split(",");
                if (data.length >= 4) {
                    try {
                        RegistoBilhetica registo = new RegistoBilhetica();
                        registo.setLote(lote);
                        registo.setLinha(linha);
                        registo.setDataHora(LocalDateTime.now());
                        registo.setParagemOrigem(data[0].trim());
                        registo.setLatitude(Double.parseDouble(data[1].trim()));
                        registo.setLongitude(Double.parseDouble(data[2].trim()));
                        registo.setTipoTitulo(data.length >= 5 ? data[4].trim() : "Passe Mensal");
                        registo.setValidacoes(Integer.parseInt(data[3].trim()));
                        registo.setZona("Braga - Urbana");
                        registos.add(registo);
                        linhasProcessadas++;
                    } catch (NumberFormatException e) {
                        System.err.println("Linha inválida ignorada: " + linha_csv);
                    }
                }
            }
        }

        registoRepository.saveAll(registos);
        System.out.println("Importação CSV concluída: " + linhasProcessadas + " registos guardados no lote " + lote.getCodigoLote());
        return linhasProcessadas;
    }
}
