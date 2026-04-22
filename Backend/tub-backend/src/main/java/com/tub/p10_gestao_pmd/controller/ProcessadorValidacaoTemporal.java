package com.tub.p10_gestao_pmd.service;

import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibidas;
import com.tub.p10_gestao_pmd.repository.RepositorioTarefasExibidasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessadorValidacaoTemporal {

    @Autowired
    private RepositorioTarefasExibidasRepository tarefasRepository;

    /**
     * UC 6.2.2 - Processar Validação Temporal
     * Este método corre automaticamente a cada 1 minuto (60000 ms) sem ninguém clicar em nada.
     */
    @Scheduled(fixedRate = 60000)
    public void validarEProcessarTarefas() {
        
        LocalDateTime horaAtual = LocalDateTime.now();

        // 1. Vai à base de dados buscar TODAS as tarefas
        List<RepositorioTarefasExibidas> todasTarefas = tarefasRepository.findAll();

        // 2. Filtra apenas as que estão pendentes e cuja hora de exibição já chegou ou já passou
        List<RepositorioTarefasExibidas> tarefasProntasAExibir = todasTarefas.stream()
                .filter(t -> !t.getConcluida()) // Só as não concluídas
                .filter(t -> !t.getDataHoraExibicao().isAfter(horaAtual)) // A hora já bateu certo
                .toList();

        // 3. Processa cada uma das tarefas encontradas
        for (RepositorioTarefasExibidas tarefa : tarefasProntasAExibir) {
            
            // LÓGICA DE ENVIO: Aqui o sistema daria a ordem para o painel mostrar a mensagem
            System.out.println("A disparar mensagem agendada! ID da Mensagem: " + tarefa.getMensagemId());

            // 4. Marca a tarefa como concluída para ela não voltar a ser processada no minuto seguinte
            tarefa.setConcluida(true);
            
            // 5. Guarda o novo estado na base de dados
            tarefasRepository.save(tarefa);
        }
    }
}