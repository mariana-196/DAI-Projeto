package com.tub.p6_auditoria.config;

import com.tub.p6_auditoria.model.RegistoAuditoria;
import com.tub.p6_auditoria.repository.RegistoAuditoriaRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalAuditFilter implements Filter {

    private final RegistoAuditoriaRepository auditoriaRepository;

    public GlobalAuditFilter(RegistoAuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String path = req.getRequestURI();
            String method = req.getMethod();
            
            // Only log API requests, skip static resources if they hit the backend
            if (path.startsWith("/api/")) {
                String ipOrigem = req.getRemoteAddr();
                if ("0:0:0:0:0:0:0:1".equals(ipOrigem)) {
                    ipOrigem = "127.0.0.1";
                }
                
                // Determine module based on path
                String modulo = "Geral";
                if (path.contains("/monitorizacao") || path.contains("/frota")) modulo = "Frota & GIS";
                else if (path.contains("/bilhetica")) modulo = "Bilhética";
                else if (path.contains("/lotacao")) modulo = "Lotação (IoT)";
                else if (path.contains("/pmd") || path.contains("/paineis")) modulo = "Painéis DMS";
                else if (path.contains("/utilizadores") || path.contains("/auth")) modulo = "Autenticação";
                else if (path.contains("/relatorios")) modulo = "Relatórios";
                else if (path.contains("/alertas")) modulo = "Alertas";
                else if (path.contains("/auditoria")) modulo = "Auditoria";

                String acao = traduzirAcao(method, path);
                
                if (acao != null) {
                    try {
                        chain.doFilter(request, response);
                    } catch (Exception e) {
                        String emailFalha = (String) req.getAttribute("utilizador_email");
                        if (emailFalha == null || emailFalha.trim().isEmpty()) {
                            emailFalha = "SISTEMA_GLOBAL";
                        }
                        gravarLog(path, acao, ipOrigem, modulo, "CRÍTICO", "Erro não tratado: " + e.getMessage(), emailFalha);
                        throw e;
                    }

                    int status = 200;
                    if (response instanceof jakarta.servlet.http.HttpServletResponse) {
                        status = ((jakarta.servlet.http.HttpServletResponse) response).getStatus();
                    }

                    String nivel = determinarNivel(acao, status);
                    String detalhes = "Ação interceptada no endpoint: " + path + " (Status HTTP: " + status + ")";
                    
                    if (acao.equals("Iniciar Sessão") && status != 200) {
                        acao = "Tentativa de Login Falhada";
                    }

                    String emailLogado = (String) req.getAttribute("utilizador_email");
                    if (emailLogado == null || emailLogado.trim().isEmpty()) {
                        emailLogado = "SISTEMA_GLOBAL";
                    }

                    gravarLog(path, acao, ipOrigem, modulo, nivel, detalhes, emailLogado);
                    return; // Retornar pois o chain.doFilter já foi executado
                }
            }
        }
        
        chain.doFilter(request, response);
    }

    private void gravarLog(String path, String acao, String ipOrigem, String modulo, String nivel, String detalhes, String utilizador) {
        RegistoAuditoria log = new RegistoAuditoria(
                utilizador, 
                acao,
                modulo,
                ipOrigem,
                nivel,
                detalhes
        );
        try {
            auditoriaRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erro ao gravar log global de auditoria: " + e.getMessage());
        }
    }

    private String determinarNivel(String acao, int status) {
        if (status >= 500) return "CRÍTICO"; // Qualquer erro
        if (acao.equals("Iniciar Sessão") && status != 200) return "AVISO"; // Tentativa falhada

        // CRITICAL
        if (acao.contains("Limpar Painel") || 
            acao.contains("Publicação Direta") || 
            acao.contains("Relatório de Auditoria") || 
            acao.contains("Exportar CSV")) {
            return "CRÍTICO";
        }

        // WARNING
        if (acao.contains("Estado de Alerta") || 
            acao.contains("Agendar") || 
            acao.contains("Cancelar Mensagem") ||
            acao.contains("Utilizador") && !acao.contains("Consultar Lista") ||
            acao.contains("Políticas de Auditoria") && !acao.contains("Consultar")) {
            return "AVISO";
        }

        // INFO (fallback e explícitos)
        return "INFO";
    }

    private String traduzirAcao(String method, String path) {
        if ("OPTIONS".equalsIgnoreCase(method)) return null;

        if (path.contains("/relatorios/auditoria")) return "Gerar Relatório de Auditoria";
        if (path.contains("/relatorios/operacional")) return "Gerar Relatório Operacional";
        if (path.contains("/relatorios/agendamentos") && "POST".equalsIgnoreCase(method)) {
            if (path.contains("/executar-agora")) return "Forçar Execução de Relatórios Agendados";
            return "Criar/Atualizar Agendamento de Relatório";
        }
        
        if (path.contains("/paineis/limpar")) return "Limpar Painéis DMS";
        if (path.contains("/paineis/publicar")) return "Publicação Direta no Painel DMS";
        if (path.contains("/paineis/agendar")) return "Agendar Publicação no Painel DMS";
        if (path.contains("/paineis/modelos") && !"GET".equalsIgnoreCase(method)) return "Criar/Editar Modelo DMS";
        
        if (path.contains("/alertas") && ("PUT".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method))) return "Alterar Estado de Alerta/Comentário";
        
        if (path.contains("/auditoria/exportar")) return "Exportar CSV de Auditoria";
        if (path.contains("/auditoria/politicas") && !"GET".equalsIgnoreCase(method)) return "Atualizar Políticas de Auditoria";
        if (path.contains("/auditoria/config-notificacoes") && !"GET".equalsIgnoreCase(method)) return "Atualizar Notificações de Auditoria";
        
        if (path.contains("/utilizadores") && !"GET".equalsIgnoreCase(method)) return "Gestão de Utilizadores (Criar/Editar/Bloquear)";
        
        if (path.contains("/auth/login") || path.contains("/auth/logout")) return null; // Já registado explicitamente pelo AuthService
        
        // Excluir todas as simples consultas/visualizações de dados (GET)
        // Só registamos GETs se forem ações explícitas de botões como Exportar/Gerar Relatório
        if ("GET".equalsIgnoreCase(method)) {
            return null;
        }

        // Fallback
        return "Modificar dados (" + path + ")";
    }
}
