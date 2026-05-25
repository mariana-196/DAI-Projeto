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
public class SecurityInterceptor implements HandlerInterceptor {

    private final ControloConsultaAuditoria auditService;

    public SecurityInterceptor(ControloConsultaAuditoria auditService) {
        this.auditService = auditService;
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
        boolean pathProtegido = uri.startsWith("/api/") && !uri.startsWith("/api/auth/");

        if (requerCargo == null && !pathProtegido) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            auditService.registar("Desconhecido", "ACESSO_NEGADO", "Autenticação", request.getRemoteAddr(), "AVISO", "Tentativa de aceder a rota protegida sem token: " + uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Token ausente ou formato inválido\"}");
            return false;
        }

        String token = authHeader.substring(7).trim();
        Map<String, Object> claims = JwtUtil.parseToken(token);
        if (claims == null) {
            auditService.registar("Desconhecido", "ACESSO_NEGADO", "Autenticação", request.getRemoteAddr(), "AVISO", "Tentativa de acesso com token inválido ou expirado na rota: " + uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Token expirado ou inválido\"}");
            return false;
        }

        String email = (String) claims.get("email");
        String cargo = (String) claims.get("cargo");
        Long id = ((Number) claims.get("id")).longValue();

        request.setAttribute("utilizador_id", id);
        request.setAttribute("utilizador_email", email);
        request.setAttribute("utilizador_cargo", cargo);

        if (requerCargo != null) {
            boolean autorizado = false;
            for (String val : requerCargo.value()) {
                if (val.equalsIgnoreCase(cargo)) {
                    autorizado = true;
                    break;
                }
            }

            if (!autorizado) {
                auditService.registar(email, "ACESSO_NEGADO", "Autorização", request.getRemoteAddr(), "AVISO", "Acesso negado para o cargo " + cargo + " na rota: " + uri);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"erro\":\"Acesso negado: cargo insuficiente\"}");
                return false;
            }
        }

        return true;
    }
}
