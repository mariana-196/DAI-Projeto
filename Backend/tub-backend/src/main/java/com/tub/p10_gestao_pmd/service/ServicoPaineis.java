package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.MensagemPMD;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;
import com.tub.p10_gestao_pmd.repository.MensagemPMDRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ServicoPaineis {

    @Autowired
    private DisplayPanelRepository repository;

    @Autowired
    private MensagemPMDRepository mensagemPMDRepository;

    // Listar para o ecrã de histórico (L98/L99)
    public List<DisplayPanel> listarTodosOsPaineis() {
        return repository.findAll();
    }

    private int getPriorityValue(String prioridade) {
        if (prioridade == null) return 2;
        return switch (prioridade.toUpperCase()) {
            case "ALTA" -> 3;
            case "MEDIA" -> 2;
            case "BAIXA" -> 1;
            default -> 2;
        };
    }

    public void checkPriority(String panelId, String novaPrioridade) {
        int newVal = getPriorityValue(novaPrioridade);
        List<MensagemPMD> ativas = mensagemPMDRepository.findAll().stream()
                .filter(m -> "ATIVA".equals(m.getEstado()) && 
                             (panelId.equals(m.getPanelId()) || 
                              "TODOS".equals(m.getPanelId()) || 
                              "TODOS".equals(panelId)))
                .toList();

        for (MensagemPMD m : ativas) {
            if (getPriorityValue(m.getPrioridade()) > newVal) {
                throw new IllegalStateException("Não é possível sobrepor uma mensagem de prioridade superior (" + m.getPrioridade() + ").");
            }
        }
    }

    /**
     * Linha 101: Persistência no painel específico
     */
    public void desativarMensagensAnterioresExcluindo(String panelId, Long msgIdExcluida) {
        List<MensagemPMD> ativas = mensagemPMDRepository.findAll().stream()
                .filter(m -> "ATIVA".equals(m.getEstado()) && 
                             (panelId.equals(m.getPanelId()) || 
                              "TODOS".equals(m.getPanelId()) || 
                              "TODOS".equals(panelId)))
                .toList();
        for (MensagemPMD m : ativas) {
            if (!m.getId().equals(msgIdExcluida)) {
                m.setEstado("INATIVA");
                mensagemPMDRepository.save(m);
            }
        }
    }

    public void desativarMensagensAnteriores(String panelId) {
        desativarMensagensAnterioresExcluindo(panelId, -1L);
    }

    public void atualizarMensagemPainelSemNovoLog(String panelId, String mensagem, String prioridade) {
        if ("TODOS".equals(panelId)) {
            List<DisplayPanel> todos = repository.findAll();
            for (DisplayPanel p : todos) {
                if (!"DEGRADADO".equals(p.getStatus())) {
                    p.setMessage(mensagem);
                    p.setPriority(prioridade);
                    p.setTimestamp(LocalDateTime.now());
                    repository.save(p);
                }
            }
        } else {
            DisplayPanel painel = repository.findById(panelId)
                    .orElseThrow(() -> new IllegalArgumentException("Painel não encontrado."));
            if ("DEGRADADO".equals(painel.getStatus())) {
                throw new IllegalStateException("Painel fora de serviço.");
            }
            painel.setMessage(mensagem);
            painel.setPriority(prioridade);
            painel.setTimestamp(LocalDateTime.now());
            repository.save(painel);
        }
    }

    /**
     * Linha 101: Persistência no painel específico
     */
    public DisplayPanel publicarMensagemNumPainel(String panelId, String mensagem, String prioridade) {
        DisplayPanel painel = repository.findById(panelId)
                .orElseThrow(() -> new IllegalArgumentException("Painel não encontrado."));

        if ("DEGRADADO".equals(painel.getStatus())) {
            throw new IllegalStateException("Painel fora de serviço.");
        }

        checkPriority(panelId, prioridade);

        // Marcar anteriores como INATIVA
        desativarMensagensAnteriores(panelId);

        // Atualiza os campos na tabela existente
        painel.setMessage(mensagem);
        painel.setPriority(prioridade != null ? prioridade : "MEDIA");
        painel.setTimestamp(LocalDateTime.now());

        // Grava no histórico (MensagemPMD ativa)
        MensagemPMD msgLog = new MensagemPMD();
        msgLog.setTitulo("Mensagem para " + panelId);
        msgLog.setConteudo(mensagem);
        msgLog.setPrioridade(prioridade != null ? prioridade : "MEDIA");
        msgLog.setEstado("ATIVA");
        msgLog.setPanelId(panelId);
        msgLog.setDataCriacao(LocalDateTime.now());
        mensagemPMDRepository.save(msgLog);

        // LINHA 101: O repository.save garante que o SQL faz o UPDATE
        return repository.save(painel);
    }

    /**
     * Linha 101: Persistência via Broadcast
     */
    public void publicarMensagemBroadcast(String mensagem, String prioridade) {
        checkPriority("TODOS", prioridade);
        List<DisplayPanel> todos = repository.findAll();
        for (DisplayPanel p : todos) {
            if (!"DEGRADADO".equals(p.getStatus())) {
                p.setMessage(mensagem);
                p.setPriority(prioridade != null ? prioridade : "MEDIA");
                p.setTimestamp(LocalDateTime.now());
                repository.save(p); // Grava na BD existente
            }
        }

        // Marcar anteriores como INATIVA
        desativarMensagensAnteriores("TODOS");

        // Grava no histórico (MensagemPMD ativa para TODOS)
        MensagemPMD msgLog = new MensagemPMD();
        msgLog.setTitulo("Mensagem Broadcast");
        msgLog.setConteudo(mensagem);
        msgLog.setPrioridade(prioridade != null ? prioridade : "MEDIA");
        msgLog.setEstado("ATIVA");
        msgLog.setPanelId("TODOS");
        msgLog.setDataCriacao(LocalDateTime.now());
        mensagemPMDRepository.save(msgLog);
    }
}
