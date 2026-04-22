package com.tub.p10_gestao_pmd.service; 

import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibicao;
import com.tub.p10_gestao_pmd.repository.RepositorioTarefasExibicaoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessadorValidacaoTemporal {

    @Autowired
    private RepositorioTarefasExibicaoRepository tarefasRepository;

    @Scheduled(fixedRate = 60000)
    public void validarEProcessarTarefas() {
        
        LocalDateTime horaAtual = LocalDateTime.now();

        
        List<RepositorioTarefasExibicao> todasTarefas = new ArrayList<>();
        
        for (Object obj : tarefasRepository.findAll()) {
            RepositorioTarefasExibicao tarefaConvertida = (RepositorioTarefasExibicao) obj;
            todasTarefas.add(tarefaConvertida);
        }
        

        List<RepositorioTarefasExibicao> tarefasProntasAExibir = todasTarefas.stream()
                .filter(t -> t.getConcluida() != null && !t.getConcluida()) 
                .filter(t -> t.getDataHoraExibicao() != null && !t.getDataHoraExibicao().isAfter(horaAtual)) 
                .collect(Collectors.toList());

        for (RepositorioTarefasExibicao tarefa : tarefasProntasAExibir) {
            System.out.println("A disparar mensagem agendada! ID da Mensagem: " + tarefa.getMensagemId());
            tarefa.setConcluida(true);
            tarefasRepository.save(tarefa);
        }
    }
}