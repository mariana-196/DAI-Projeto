package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.PrevisaoChegada;
import com.tub.p10_gestao_pmd.model.PainelPMD;
import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;

import com.tub.p10_gestao_pmd.repository.PrevisaoChegadaRepository;
import com.tub.p10_gestao_pmd.repository.PainelPMDRepository;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;
import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrevisaoService {

    @Autowired
    private PrevisaoChegadaRepository previsaoRepository;

    @Autowired
    private PainelPMDRepository painelPMDRepository;

    @Autowired
    private DisplayPanelRepository displayPanelRepository;

    @Autowired
    private LinhaRepository linhaRepository;

    @Autowired
    private ViaturasRepository viaturasRepository;

    private static final double TEMPO_MEDIO_PARAGEM = 2.5;

    public PrevisaoChegada calcularEGuardarPrevisao(Long viaturaId, Long painelId, Long linhaId, String destino, int paragensRestantes) {
        
        Viatura viatura = viaturasRepository.findById(viaturaId)
                .orElseThrow(() -> new IllegalArgumentException("Viatura com ID " + viaturaId + " não encontrada."));

        PainelPMD painel = painelPMDRepository.findById(painelId)
                .orElseThrow(() -> new IllegalArgumentException("Painel PMD com ID " + painelId + " não encontrado."));

        Linha linha = linhaRepository.findById(linhaId)
                .orElseThrow(() -> new IllegalArgumentException("Linha com ID " + linhaId + " não encontrada."));

        if (paragensRestantes < 0) {
            throw new IllegalArgumentException("O número de paragens restantes não pode ser negativo.");
        }

        int tempoFinal = (int) Math.round(paragensRestantes * TEMPO_MEDIO_PARAGEM);

        PrevisaoChegada previsao = new PrevisaoChegada();
        previsao.setViatura(viatura);
        previsao.setPainel(painel);
        previsao.setLinha(linha);
        previsao.setDestino(destino);
        previsao.setEtaMinutos(tempoFinal); 
        previsao.setTimestamp(LocalDateTime.now());

        PrevisaoChegada savedPrevisao = previsaoRepository.save(previsao);

        // Atualizar imediatamente a mensagem de visualização pública do painel correspondente (DisplayPanel)
        atualizarMensagemPainelComPrevisoes(painelId, painel.getCodigo());

        return savedPrevisao;
    }

    public void atualizarMensagemPainelComPrevisoes(Long painelId, String panelCodigo) {
        DisplayPanel displayPanel = displayPanelRepository.findById(panelCodigo).orElse(null);
        if (displayPanel != null) {
            if ("DEGRADADO".equals(displayPanel.getStatus()) || "OFFLINE".equals(displayPanel.getStatus())) {
                // Se o painel está degradado ou offline, não altera a sua mensagem padrão de erro
                return;
            }

            // Buscar previsões recentes dos últimos 15 minutos para este painel utilizando query otimizada
            LocalDateTime limiteRecente = LocalDateTime.now().minusMinutes(15);
            List<PrevisaoChegada> previsoesAtivas = previsaoRepository.findByPainelIdAndTimestampAfter(painelId, limiteRecente).stream()
                    .sorted((p1, p2) -> p1.getEtaMinutos().compareTo(p2.getEtaMinutos()))
                    .collect(Collectors.toList());

            if (!previsoesAtivas.isEmpty()) {
                // Construir string de previsões (ex: "L43: 5 MIN | L02: 8 MIN")
                String msg = previsoesAtivas.stream()
                        .map(p -> {
                            String cod = p.getLinha() != null ? p.getLinha().getCodigo() : "??";
                            return "L" + cod + ": " + p.getEtaMinutos() + " MIN";
                        })
                        .distinct()
                        .collect(Collectors.joining(" | "));
                
                displayPanel.setMessage(msg);
            } else {
                displayPanel.setMessage("Sem autocarros previstos de momento.");
            }
            displayPanel.setTimestamp(LocalDateTime.now());
            displayPanelRepository.save(displayPanel);
        }
    }

    public List<PrevisaoChegada> obterPrevisoesDaParagem(Long painelId) {
        // Obter apenas as previsões mais recentes dos últimos 15 minutos para serem exibidas no ecrã público
        LocalDateTime limiteRecente = LocalDateTime.now().minusMinutes(15);
        return previsaoRepository.findByPainelIdAndTimestampAfter(painelId, limiteRecente).stream()
                .sorted((p1, p2) -> p1.getEtaMinutos().compareTo(p2.getEtaMinutos()))
                .collect(Collectors.toList());
    }

    public boolean isStatusOk() {
        return true;
    }

    public List<PainelPMD> obterTodosOsPaineisPMD() {
        return painelPMDRepository.findAll();
    }

    public List<Linha> obterTodasAsLinhas() {
        return linhaRepository.findAll();
    }
}