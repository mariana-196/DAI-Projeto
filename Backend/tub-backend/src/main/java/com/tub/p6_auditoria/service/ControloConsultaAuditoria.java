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
    private final com.tub.p6_auditoria.repository.PoliticasAuditoriaRepository politicasAuditoriaRepository;

    private static final java.util.Map<String, Integer> NIVEL_PRIORIDADE = java.util.Map.of(
            "INFO", 0, "AVISO", 1, "WARNING", 1, "ERRO", 2, "ERROR", 2, "CRÍTICO", 3, "CRITICO", 3, "CRITICAL", 3
    );

    public ControloConsultaAuditoria(
            RegistoAuditoriaRepository registoAuditoriaRepository,
            RegraNotificacaoRepository regraNotificacaoRepository,
            com.tub.p6_auditoria.repository.PoliticasAuditoriaRepository politicasAuditoriaRepository) {
        this.registoAuditoriaRepository = registoAuditoriaRepository;
        this.regraNotificacaoRepository = regraNotificacaoRepository;
        this.politicasAuditoriaRepository = politicasAuditoriaRepository;
    }

    private boolean deveRegistarLog(String nivel) {
        if (nivel == null) return true;
        com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria politica = politicasAuditoriaRepository.findAll().stream().findFirst().orElse(null);
        if (politica != null && politica.getNivelMinimo() != null) {
            int nivelLog = NIVEL_PRIORIDADE.getOrDefault(nivel.toUpperCase(), 0);
            int nivelMinimo = NIVEL_PRIORIDADE.getOrDefault(politica.getNivelMinimo().toUpperCase(), 0);
            return nivelLog >= nivelMinimo;
        }
        return true;
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

    private String removerAcentos(String str) {
        if (str == null) return null;
        return str.replace("á", "a").replace("Á", "A")
                  .replace("à", "a").replace("À", "A")
                  .replace("ã", "a").replace("Ã", "A")
                  .replace("â", "a").replace("Â", "A")
                  .replace("é", "e").replace("É", "E")
                  .replace("ê", "e").replace("Ê", "E")
                  .replace("í", "i").replace("Í", "I")
                  .replace("ó", "o").replace("Ó", "O")
                  .replace("õ", "o").replace("Õ", "O")
                  .replace("ô", "o").replace("Ô", "O")
                  .replace("ú", "u").replace("Ú", "U")
                  .replace("ç", "c").replace("Ç", "C");
    }

    private String normalizarIp(String ip) {
        if (ip == null) return "127.0.0.1";
        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
            return "127.0.0.1";
        }
        return ip;
    }

    private String normalizarAcao(String acao) {
        if (acao == null) return "Acao Desconhecida";
        
        String acaoSemAcentos = removerAcentos(acao);
        String acaoUpper = acaoSemAcentos.toUpperCase();
        
        switch (acaoUpper) {
            case "LOGIN_SUCESSO":
            case "INICIO DE SESSO":
            case "INICIO DE SESSAO":
                return "Inicio de Sessao";
            case "LOGOUT":
                return "Fim de Sessao";
            case "FALHA DE AUTENTICACAO":
            case "FALHA_AUTENTICACAO":
                return "Falha de Autenticacao";
            case "CONTA_BLOQUEADA":
            case "CONTA BLOQUEADA":
                return "Conta Bloqueada";
            case "ACESSO_NEGADO":
                return "Acesso Negado";
            case "CRIAR_UTILIZADOR":
                return "Criar Utilizador";
            case "EDITAR_UTILIZADOR":
                return "Editar Utilizador";
            case "DESATIVAR_UTILIZADOR":
                return "Desativar Utilizador";
            case "ATIVAR_UTILIZADOR":
                return "Ativar Utilizador";
            case "ELIMINAR_UTILIZADOR":
                return "Eliminar Utilizador";
            case "SINCRONIZAR_BILHETICA":
                return "Sincronizacao Bilhetica";
            case "IMPORTACAO_BILHETICA":
                return "Importacao Bilhetica";
            case "EXPORTAR_CSV":
                return "Exportacao(CSV)";
            case "EXPORTAR_PDF":
                return "Exportacao(PDF)";
            default:
                String acaoProcessada = acaoSemAcentos;
                if (acaoProcessada.contains("_")) {
                    String[] parts = acaoProcessada.split("_");
                    StringBuilder sb = new StringBuilder();
                    for (String part : parts) {
                        if (!part.isEmpty()) {
                            sb.append(Character.toUpperCase(part.charAt(0)));
                            if (part.length() > 1) {
                                sb.append(part.substring(1).toLowerCase());
                            }
                            sb.append(" ");
                        }
                    }
                    return sb.toString().trim();
                }
                
                if (acaoProcessada.equals(acaoUpper)) {
                    StringBuilder sb = new StringBuilder();
                    String[] parts = acaoProcessada.split("\\s+");
                    for (String part : parts) {
                        if (!part.isEmpty()) {
                            sb.append(Character.toUpperCase(part.charAt(0)));
                            if (part.length() > 1) {
                                sb.append(part.substring(1).toLowerCase());
                            }
                            sb.append(" ");
                        }
                    }
                    return sb.toString().trim();
                }
                
                return acaoProcessada;
        }
    }

    public RegistoAuditoria guardarLog(RegistoAuditoria registo) {
        Objects.requireNonNull(registo, "O registo de auditoria não pode ser null");
        try {
            if (!deveRegistarLog(registo.getNivel())) {
                return registo;
            }
            registo.setAcao(normalizarAcao(registo.getAcao()));
            registo.setIpOrigem(normalizarIp(registo.getIpOrigem()));
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
            if (!deveRegistarLog(nivel)) {
                return;
            }
            RegistoAuditoria registo = new RegistoAuditoria(
                    utilizador, normalizarAcao(acao), modulo, normalizarIp(ipOrigem), nivel, detalhe
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