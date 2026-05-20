package com.tub.p1_autenticacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tub.p1_autenticacao.service.ControloSegurancaAutenticacao;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ControladorAcessos {

    private final ControloSegurancaAutenticacao authService;

    public ControladorAcessos(ControloSegurancaAutenticacao authService) {
        this.authService = authService;
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

        return ResponseEntity.ok(new LoginResponse(
                resultado.getMensagem(),
                resultado.getToken(),
                resultado.getUtilizador()
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

        return ResponseEntity.ok(new LoginResponse(
                resultado.getMensagem(),
                resultado.getToken(),
                resultado.getUtilizador()
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