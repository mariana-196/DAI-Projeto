package com.tub.p6_auditoria.service;

import com.tub.p6_auditoria.model.RegistoAuditoria;
import com.tub.p6_auditoria.model.RegraNotificacao;
import com.tub.p6_auditoria.repository.RegistoAuditoriaRepository;
import com.tub.p6_auditoria.repository.RegraNotificacaoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service("servicoConsultaAuditoria")
public class ControloConsultaAuditoria {

    private final RegistoAuditoriaRepository registoAuditoriaRepository;
    private final RegraNotificacaoRepository regraNotificacaoRepository;

    // Construtor atualizado para injetar também o repositório de regras de notificação
    public ControloConsultaAuditoria(
            RegistoAuditoriaRepository registoAuditoriaRepository,
            RegraNotificacaoRepository regraNotificacaoRepository) {
        this.registoAuditoriaRepository = registoAuditoriaRepository;
        this.regraNotificacaoRepository = regraNotificacaoRepository;
    }

    public List<RegistoAuditoria> pesquisarLogs(
            String utilizador,
            String acao,
            String modulo,
            String nivel,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return registoAuditoriaRepository.pesquisarComFiltros(
                utilizador, acao, modulo, nivel, dataInicio, dataFim
        );
    }

    public RegistoAuditoria guardarLog(RegistoAuditoria registo) {
        Objects.requireNonNull(registo, "O registo de auditoria não pode ser null");
        try {
            RegistoAuditoria logGuardado = registoAuditoriaRepository.save(registo);
            
            // Verifica se este log precisa de disparar algum alerta configurado
            verificarEDispararAlertas(logGuardado);
            
            return logGuardado;
        } catch (Exception e) {
            System.err.println("Erro ao guardar log de auditoria: " + e.getMessage());
            e.printStackTrace();
            return registo;
        }
    }

    public void registar(String utilizador, String acao, String modulo, String ipOrigem, String nivel, String detalhe) {
        try {
            RegistoAuditoria registo = new RegistoAuditoria(
                    utilizador, acao, modulo, ipOrigem, nivel, detalhe
            );
            RegistoAuditoria logGuardado = registoAuditoriaRepository.save(registo);
            
            // Verifica se este log precisa de disparar algum alerta configurado
            verificarEDispararAlertas(logGuardado);
        } catch (Exception e) {
            System.err.println("Erro ao registar log de auditoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lógica que cruza o log gerado com as regras configuradas pelo administrador no Frontend
     */
    private void verificarEDispararAlertas(RegistoAuditoria log) {
        // Só faz sentido validar se o log for de aviso ou crítico
        if ("CRÍTICO".equalsIgnoreCase(log.getNivel()) || "AVISO".equalsIgnoreCase(log.getNivel()) || "CRITICO".equalsIgnoreCase(log.getNivel())) {
            
            // Procura todas as regras de notificação criadas no sistema
            List<RegraNotificacao> regras = regraNotificacaoRepository.findAll();
            
            for (RegraNotificacao regra : regras) {
                // Se a regra estiver ativa e o tipo de evento coincidir com a ação realizada (ex: "Falha" ou "Exportação")
                if (regra.getAtiva() && (log.getAcao().toLowerCase().contains(regra.getTipoEvento().toLowerCase()) || log.getNivel().equalsIgnoreCase(regra.getSeveridadeMinima()))) {
                    
                    // Simulação visual clara na consola do envio do email/alerta
                    System.out.println("\n==================================================================");
                    System.out.println("🚨 [SISTEMA DE SEGURANÇA - TUB] ALERTA DISPARADO EM TEMPO REAL");
                    System.out.println("⚠️  Severidade detetada: " + log.getNivel());
                    System.out.println("📧 Destinatário Configurado: " + regra.getDestinatario());
                    System.out.println("💻 Canal de Envio: " + regra.getCanal());
                    System.out.println("📝 Descrição da Ocorrência: " + log.getDetalhe() + " (Módulo: " + log.getModulo() + ")");
                    System.out.println("👤 Utilizador Associado: " + log.getUtilizador() + " | IP: " + log.getIpOrigem());
                    System.out.println("==================================================================\n");
                }
            }
        }
    }
}