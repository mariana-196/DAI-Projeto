package com.tub.controller;

import com.tub.model.Utilizador;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/utilizadores")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Permite que o teu HTML (Frontend) fale com o Java
public class UtilizadorController {

    // Esta lista finge que é a nossa base de dados por agora
    private static List<Utilizador> listaFicticia = new ArrayList<>();

    // Ao iniciar, vamos meter logo os dois que o teu colega tinha no HTML
    static {
        listaFicticia.add(new Utilizador("Carlos Silva", "admin@tub.pt", "1234", "ADMIN"));
        listaFicticia.add(new Utilizador("Ana Pereira", "operador1@tub.pt", "1234", "OPERADOR"));
    }

    // Listar todos para o site
    @GetMapping
    public List<Utilizador> listar() {
        return listaFicticia;
    }

    // Guardar um novo utilizador
    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody Utilizador novoUser) {
        // UC1.2 Fluxo A1: Verificar se o email já existe na nossa lista
        for (Utilizador u : listaFicticia) {
            if (u.getEmail().equals(novoUser.getEmail())) {
                return ResponseEntity.badRequest().body("Este email já existe na lista local!");
            }
        }

        listaFicticia.add(novoUser);
        System.out.println("Utilizador guardado na memória: " + novoUser.getNome());
        return ResponseEntity.ok(novoUser);
    }
}