package com.tub.p7_relatorios.service;

import com.tub.p7_relatorios.model.TarefaAgendada;
import com.tub.p7_relatorios.repository.TarefaAgendadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ControloTarefasAutomaticas {

    @Autowired
    private TarefaAgendadaRepository tarefaRepository;

    // A anotação @Scheduled faz com que isto corra sozinho (neste caso, todos os dias às 02:00 da manhã)
    @Scheduled(cron = "0 0 2 * * ?")
    public void executarAgendamentoAutomatico() {
        
        // 1. Regista na base de dados que a tarefa arrancou
        TarefaAgendada novaTarefa = new TarefaAgendada("GeracaoAutomaticaRelatorioValidacoes", "EM_EXECUCAO");
        tarefaRepository.save(novaTarefa);

        try {
            // Aqui entraria a lógica real de compilar o PDF ou Excel do relatório
            // Como é um UC de simulação, passamos direto para sucesso:
            novaTarefa.setEstado("SUCESSO");
            
        } catch (Exception e) {
            // Se algo falhar, a base de dados fica a saber
            novaTarefa.setEstado("FALHA");
            
        } finally {
            // 2. Atualiza o estado final na base de dados (Passo 7.4 do vosso 4SRS)
            tarefaRepository.save(novaTarefa);
        }
    }
}