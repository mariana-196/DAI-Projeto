package com.tub.p7_relatorios.service;

import com.tub.p7_relatorios.model.TarefaAgendada;
import com.tub.p7_relatorios.repository.TarefaAgendadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ControloTarefasAutomaticas {

    @Autowired
    private TarefaAgendadaRepository tarefaRepository;

    // A anotação @Scheduled faz com que isto corra sozinho (neste caso, todos os dias às 02:00 da manhã)
    @Scheduled(cron = "0 0 2 * * ?")
    public void executarAgendamentoAutomatico() {
        List<TarefaAgendada> tarefas = tarefaRepository.findAll();
        boolean executouAlguma = false;

        for (TarefaAgendada tarefa : tarefas) {
            // Se for um agendamento ativo e tiver parâmetros configurados (emailDestinatario não nulo)
            if (tarefa.isAtivo() && tarefa.getEmailDestinatario() != null) {
                executouAlguma = true;
                tarefa.setEstado("EM_EXECUCAO");
                tarefa.setDataHoraExecucao(LocalDateTime.now());
                tarefaRepository.save(tarefa);

                try {
                    // Lógica simulada de geração e envio por e-mail do relatório
                    System.out.println("A executar agendamento: " + tarefa.getNomeTarefa() 
                            + " | Enviar para: " + tarefa.getEmailDestinatario() 
                            + " | Formato: " + tarefa.getFormato() 
                            + " | Tipo: " + tarefa.getTipoRelatorio());
                    
                    tarefa.setEstado("SUCESSO");
                } catch (Exception e) {
                    tarefa.setEstado("FALHA");
                } finally {
                    tarefaRepository.save(tarefa);
                }
            }
        }

        // Se não houver tarefas dinâmicas configuradas, executa o comportamento por omissão
        if (!executouAlguma) {
            TarefaAgendada novaTarefa = new TarefaAgendada("GeracaoAutomaticaRelatorioValidacoes", "EM_EXECUCAO");
            tarefaRepository.save(novaTarefa);

            try {
                novaTarefa.setEstado("SUCESSO");
            } catch (Exception e) {
                novaTarefa.setEstado("FALHA");
            } finally {
                tarefaRepository.save(novaTarefa);
            }
        }
    }
}