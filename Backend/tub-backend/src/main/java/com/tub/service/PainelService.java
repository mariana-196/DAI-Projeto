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

    /**
     * Linha 100: Retorna todos os painéis para o ecrã principal e para o histórico
     */
    public List<DisplayPanel> listarTodosOsPaineis() {
        return repository.findAll();
    }

    /**
     * Linha 100: Lógica para publicar num painel específico
     */
    public DisplayPanel publicarMensagemNumPainel(String panelId, String mensagem) {
        // 1. Tenta encontrar o painel na base de dados
        DisplayPanel painel = repository.findById(panelId)
                .orElseThrow(() -> new IllegalArgumentException("Painel não encontrado: " + panelId));

        // 2. Regra de Negócio: Não permite publicar se o hardware estiver offline
        if ("DEGRADADO".equals(painel.getStatus()) || "OFFLINE".equals(painel.getStatus())) {
            throw new IllegalStateException("O painel " + panelId + " está fora de serviço.");
        }

        // 3. Atualiza os dados
        painel.setMessage(mensagem);
        painel.setTimestamp(LocalDateTime.now());

        // 4. Grava a alteração (isto cria o registo no histórico)
        return repository.save(painel);
    }

    /**
     * Linha 100: Lógica para enviar para todos os painéis ativos (Broadcast)
     */
    public void publicarMensagemBroadcast(String mensagem) {
        List<DisplayPanel> todosOsPaineis = repository.findAll();

        for (DisplayPanel painel : todosOsPaineis) {
            // Só atualiza os que não estão avariados
            if (!"DEGRADADO".equals(painel.getStatus()) && !"OFFLINE".equals(painel.getStatus())) {
                painel.setMessage(mensagem);
                painel.setTimestamp(LocalDateTime.now());
                repository.save(painel);
            }
        }
    }
}
