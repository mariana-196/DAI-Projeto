package com.tub.p1_autenticacao.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.method.HandlerMethod;

import com.tub.p1_autenticacao.util.JwtUtil;
import com.tub.p1_autenticacao.annotation.RequerCargo;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.util.Map;

@Component
public class InterceptorSeguranca implements HandlerInterceptor {

    private final ControloConsultaAuditoria auditService;
    private final com.tub.p1_autenticacao.repository.RegistoUtilizadorRepository utilizadorRepository;

    public InterceptorSeguranca(
            ControloConsultaAuditoria auditService,
            com.tub.p1_autenticacao.repository.RegistoUtilizadorRepository utilizadorRepository
    ) {
        this.auditService = auditService;
        this.utilizadorRepository = utilizadorRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequerCargo requerCargo = handlerMethod.getMethodAnnotation(RequerCargo.class);
        if (requerCargo == null) {
            requerCargo = handlerMethod.getBeanType().getAnnotation(RequerCargo.class);
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Check if it's a public GET endpoint
        boolean isPublicGet = "GET".equalsIgnoreCase(method) && (
                uri.equals("/api/paineis") ||
                uri.equals("/api/previsoes/paineis-pmd") ||
                uri.startsWith("/api/previsoes/consulta/")
        );

        boolean pathProtegido = uri.startsWith("/api/") && !uri.startsWith("/api/auth/") && !isPublicGet;

        if (requerCargo == null && !pathProtegido) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                auditService.registar("Desconhecido", "ACESSO_NEGADO", "Autenticação", request.getRemoteAddr(), "AVISO", "Tentativa de aceder a rota protegida sem token: " + uri);
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Token ausente ou formato inválido\"}");
            return false;
        }

        String token = authHeader.substring(7).trim();
        Map<String, Object> claims = JwtUtil.parseToken(token);
        if (claims == null) {
            try {
                auditService.registar("Desconhecido", "ACESSO_NEGADO", "Autenticação", request.getRemoteAddr(), "AVISO", "Tentativa de acesso com token inválido ou expirado na rota: " + uri);
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Token expirado ou inválido\"}");
            return false;
        }

        String email = (String) claims.get("email");
        String cargo = (String) claims.get("cargo");
        Long id = ((Number) claims.get("id")).longValue();

        java.util.Optional<com.tub.p1_autenticacao.model.RegistoUtilizador> userOpt = utilizadorRepository.findById(id);
        if (userOpt.isEmpty() || !userOpt.get().isAtivo()) {
            try {
                auditService.registar(email != null ? email : "Inativo", "ACESSO_NEGADO", "Autenticação", request.getRemoteAddr(), "AVISO", "Tentativa de acesso de utilizador inativo ou bloqueado: " + uri);
            } catch (Exception e) {
                System.err.println("Erro ao registar auditoria: " + e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Conta inativa ou bloqueada\"}");
            return false;
        }

        request.setAttribute("utilizador_id", id);
        request.setAttribute("utilizador_email", email);
        request.setAttribute("utilizador_cargo", cargo);

        String[] cargosPermitidos = requerCargo != null
                ? requerCargo.value()
                : cargosPermitidosPorRota(uri);

        if (cargosPermitidos != null) {
            boolean autorizado = cargoAutorizado(cargo, cargosPermitidos);

            if (!autorizado) {
                try {
                    auditService.registar(email, "ACESSO_NEGADO", "Autorização", request.getRemoteAddr(), "AVISO", "Acesso negado para o cargo " + cargo + " na rota: " + uri);
                } catch (Exception e) {
                    System.err.println("Erro ao registar auditoria: " + e.getMessage());
                }
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"erro\":\"Acesso negado: cargo insuficiente\"}");
                return false;
            }
        }

        return true;
    }

    private boolean cargoAutorizado(String cargo, String[] cargosPermitidos) {
        if (cargo == null) {
            return false;
        }

        for (String val : cargosPermitidos) {
            if (val.equalsIgnoreCase(cargo)) {
                return true;
            }
        }
        return false;
    }

    private String[] cargosPermitidosPorRota(String uri) {
        if (uri.startsWith("/api/auditoria")
                || uri.startsWith("/api/regras-auditoria")
                || uri.startsWith("/api/relatorios/auditoria")
                || uri.startsWith("/api/relatorios/agendamentos")
                || (uri.startsWith("/api/utilizadores") && !uri.equals("/api/utilizadores") && !uri.matches("^/api/utilizadores/\\d+$"))) {
            return new String[] {"ADMINISTRADOR"};
        }

        return null;
    }
}
