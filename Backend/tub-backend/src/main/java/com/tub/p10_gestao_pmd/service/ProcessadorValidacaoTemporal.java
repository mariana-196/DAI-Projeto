package com.tub.p10_gestao_pmd.service; 

import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibicao;
import com.tub.p10_gestao_pmd.repository.RepositorioTarefasExibicaoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // O import salva-vidas para as listas

import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibicao;

@Service
public class ProcessadorValidacaoTemporal {

    @Autowired
    private RepositorioTarefasExibicaoRepository tarefasRepository;

    /**
     * UC 6.2.2 - Processar Validação Temporal
     * Corre automaticamente a cada 1 minuto (60000 ms)
     */
    @Scheduled(fixedRate = 60000)
    public void validarEProcessarTarefas() {
        
        LocalDateTime horaAtual = LocalDateTime.now();

        // 1. Vai à BD buscar TODAS as tarefas
        List<RepositorioTarefasExibicao> todasTarefas = tarefasRepository.findAll();

        // 2. Filtra as pendentes e cuja hora de exibição já chegou
        // Correção: Usar o Collectors.toList() para evitar o erro do Java
        List<RepositorioTarefasExibicao> tarefasProntasAExibir = todasTarefas.stream()
                .filter(t -> !t.getConcluida()) 
                .filter(t -> !t.getDataHoraExibicao().isAfter(horaAtual)) 
                .collect(Collectors.toList());

        // 3. Processa cada tarefa
        for (RepositorioTarefasExibicao tarefa : tarefasProntasAExibir) {
            
            System.out.println("A disparar mensagem agendada! ID da Mensagem: " + tarefa.getMensagemId());

            // 4. Marca a tarefa como concluída
            tarefa.setConcluida(true);
            
            // 5. Atualiza a BD
            tarefasRepository.save(tarefa);
        }
    }
}