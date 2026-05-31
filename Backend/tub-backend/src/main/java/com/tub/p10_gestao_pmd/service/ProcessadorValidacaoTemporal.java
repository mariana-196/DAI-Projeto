package com.tub.p10_gestao_pmd.service; 

import com.tub.p10_gestao_pmd.model.MensagemPMD;
import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibicao;
import com.tub.p10_gestao_pmd.repository.MensagemPMDRepository;
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

    @Autowired
    private MensagemPMDRepository mensagemRepository;

    @Autowired
    private ServicoPaineis painelService;

    @Scheduled(fixedRate = 10000) // Rodar a cada 10 segundos para maior responsividade no simulador/testes
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
            
            MensagemPMD msg = mensagemRepository.findById(tarefa.getMensagemId()).orElse(null);
            if (msg != null) {
                try {
                    // Verificar se existe uma mensagem de prioridade superior ativa no painel
                    painelService.checkPriority(msg.getPanelId(), msg.getPrioridade());

                    // Publicar mensagem no painel correspondente sem duplicar log
                    painelService.atualizarMensagemPainelSemNovoLog(msg.getPanelId(), msg.getConteudo(), msg.getPrioridade());

                    // Desativar outras mensagens ativas
                    painelService.desativarMensagensAnterioresExcluindo(msg.getPanelId(), msg.getId());

                    // Atualizar estado da mensagem original para ativa com timestamp atual
                    msg.setEstado("ATIVA");
                    msg.setDataCriacao(LocalDateTime.now());
                    mensagemRepository.save(msg);

                    tarefa.setConcluida(true);
                    tarefasRepository.save(tarefa);

                } catch (IllegalStateException e) {
                    if (e.getMessage().contains("prioridade superior")) {
                        System.out.println("A aguardar. Mensagem de prioridade superior ativa no painel " + msg.getPanelId());
                        // Não marcar como concluída, manter pendente para o próximo ciclo
                    } else {
                        System.err.println("Erro de estado (Painel degradado?) " + msg.getId() + ": " + e.getMessage());
                        tarefa.setConcluida(true);
                        tarefasRepository.save(tarefa);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao publicar mensagem agendada " + msg.getId() + ": " + e.getMessage());
                    tarefa.setConcluida(true);
                    tarefasRepository.save(tarefa);
                }
            } else {
                tarefa.setConcluida(true);
                tarefasRepository.save(tarefa);
            }
        }
    }
}