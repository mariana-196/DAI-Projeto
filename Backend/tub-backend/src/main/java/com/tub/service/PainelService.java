package com.tub.service;

import com.tub.model.DisplayPanel;
import com.tub.repository.DisplayPanelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PainelService {

    @Autowired
    private DisplayPanelRepository repository;

    // Listar para o ecrã de histórico (L98/L99)
    public List<DisplayPanel> listarTodosOsPaineis() {
        return repository.findAll();
    }

    /**
     * Linha 101: Persistência no painel específico
     */
    public DisplayPanel publicarMensagemNumPainel(String panelId, String mensagem) {
        DisplayPanel painel = repository.findById(panelId)
                .orElseThrow(() -> new IllegalArgumentException("Painel não encontrado."));

        if ("DEGRADADO".equals(painel.getStatus())) {
            throw new IllegalStateException("Painel fora de serviço.");
        }

        // Atualiza os campos na tabela existente
        painel.setMessage(mensagem);
        painel.setTimestamp(LocalDateTime.now());

        // LINHA 101: O repository.save garante que o SQL faz o UPDATE
        return repository.save(painel);
    }

    /**
     * Linha 101: Persistência via Broadcast
     */
    public void publicarMensagemBroadcast(String mensagem) {
        List<DisplayPanel> todos = repository.findAll();
        for (DisplayPanel p : todos) {
            if (!"DEGRADADO".equals(p.getStatus())) {
                p.setMessage(mensagem);
                p.setTimestamp(LocalDateTime.now());
                repository.save(p); // Grava na BD existente
            }
        }
    }
}
