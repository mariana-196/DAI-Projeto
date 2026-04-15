package com.tub.controller;

import com.tub.model.Utilizador;
import com.tub.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Utilizador utilizador = authService.autenticar(request.getEmail(), request.getPassword());

        if (utilizador == null) {
            return ResponseEntity.status(401).body("Credenciais inválidas ou conta desativada.");
        }

        return ResponseEntity.ok(utilizador);
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}