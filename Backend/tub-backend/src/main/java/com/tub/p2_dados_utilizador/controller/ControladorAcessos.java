package com.tub.p2_dados_utilizador.controller;

import com.tub.p1_autenticacao.model.RegistoUtilizador;
import com.tub.p1_autenticacao.repository.RegistoUtilizadorRepository;
import com.tub.p1_autenticacao.annotation.RequerCargo;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import com.tub.p1_autenticacao.service.ControloSegurancaAutenticacao;
import com.tub.p1_autenticacao.util.JwtUtil;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ControladorAcessos {

    private final ControloSegurancaAutenticacao authService;
    private final ControloConsultaAuditoria auditService;

    public ControladorAcessos(
            ControloSegurancaAutenticacao authService,
            ControloConsultaAuditoria auditService
    ) {
        this.authService = authService;
        this.auditService = auditService;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (claims != null) {
                String email = (String) claims.get("email");
                try {
                    auditService.registar(
                            email,
                            "LOGOUT",
                            "Autenticação",
                            "127.0.0.1",
                            "INFO",
                            "Sessão encerrada com sucesso"
                    );
                } catch (Exception e) {
                    System.err.println("Erro ao registar auditoria: " + e.getMessage());
                }
            }
        }
        return ResponseEntity.ok(Map.of("status", "Sucesso", "mensagem", "Sessão encerrada com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        ControloSegurancaAutenticacao.ResultadoAutenticacao resultado =
                authService.autenticar(request.getEmail(), request.getPassword());

        if (!resultado.isSucesso()) {
            return ResponseEntity.status(401).body(new LoginResponse(
                    resultado.getMensagem(),
                    null,
                    null
            ));
        }

        Map<String, Object> utilizadorSafe = null;
        if (resultado.getUtilizador() != null) {
            utilizadorSafe = new HashMap<>();
            utilizadorSafe.put("id", resultado.getUtilizador().getId());
            utilizadorSafe.put("nome", resultado.getUtilizador().getNome());
            utilizadorSafe.put("email", resultado.getUtilizador().getEmail());
            utilizadorSafe.put("cargo", resultado.getUtilizador().getCargo());
        }

        return ResponseEntity.ok(new LoginResponse(
                resultado.getMensagem(),
                resultado.getToken(),
                utilizadorSafe
        ));
    }

    @PostMapping("/login-gov")
    public ResponseEntity<?> loginGov(@RequestBody LoginGovRequest request) {
        ControloSegurancaAutenticacao.ResultadoAutenticacao resultado =
                authService.autenticarComGov(request.getEmail(), request.getCodigoGov());

        if (!resultado.isSucesso()) {
            return ResponseEntity.status(401).body(new LoginResponse(
                    resultado.getMensagem(),
                    null,
                    null
            ));
        }

        Map<String, Object> utilizadorSafe = null;
        if (resultado.getUtilizador() != null) {
            utilizadorSafe = new HashMap<>();
            utilizadorSafe.put("id", resultado.getUtilizador().getId());
            utilizadorSafe.put("nome", resultado.getUtilizador().getNome());
            utilizadorSafe.put("email", resultado.getUtilizador().getEmail());
            utilizadorSafe.put("cargo", resultado.getUtilizador().getCargo());
        }

        return ResponseEntity.ok(new LoginResponse(
                resultado.getMensagem(),
                resultado.getToken(),
                utilizadorSafe
        ));
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginGovRequest {
        private String email;
        private String codigoGov;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCodigoGov() { return codigoGov; }
        public void setCodigoGov(String codigoGov) { this.codigoGov = codigoGov; }
    }

    public static class LoginResponse {
        private String mensagem;
        private String token;
        private Object utilizador;

        public LoginResponse(String mensagem, String token, Object utilizador) {
            this.mensagem = mensagem;
            this.token = token;
            this.utilizador = utilizador;
        }

        public String getMensagem() { return mensagem; }
        public String getToken() { return token; }
        public Object getUtilizador() { return utilizador; }
    }
}