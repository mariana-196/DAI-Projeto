package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite a ligação entre o browser e o Java
public class AuthController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();

        // Validação real no Backend
        if ("admin@tub.pt".equals(email) && "1234".equals(password)) {
            response.put("success", true);
            response.put("message", "Acesso autorizado!");
        } else {
            response.put("success", false);
            response.put("message", "Credenciais inválidas.");
        }

        return response;
    }
}