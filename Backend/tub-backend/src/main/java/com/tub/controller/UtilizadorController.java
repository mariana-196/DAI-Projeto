package com.tub.controller;

import com.tub.model.Utilizador;
import com.tub.repository.UtilizadorRepository;
import com.tub.service.AutorizacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilizadores")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UtilizadorController {

    private final UtilizadorRepository utilizadorRepository;
    private final AutorizacaoService autorizacaoService;

    public UtilizadorController(UtilizadorRepository utilizadorRepository, AutorizacaoService autorizacaoService) {
        this.utilizadorRepository = utilizadorRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @GetMapping
    public List<Utilizador> listar() {
        return utilizadorRepository.findAll();
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Utilizador novoUser
    ) {
        String token = extrairToken(authorization);

        if (!autorizacaoService.eAdmin(token)) {
            return ResponseEntity.status(403).body("Apenas administradores podem criar utilizadores.");
        }

        Optional<Utilizador> existente = utilizadorRepository.findByEmail(novoUser.getEmail());

        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("Este email já existe!");
        }

        utilizadorRepository.save(novoUser);
        return ResponseEntity.ok(novoUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @RequestBody Utilizador dadosAtualizados
    ) {
        String token = extrairToken(authorization);

        if (!autorizacaoService.eAdmin(token)) {
            return ResponseEntity.status(403).body("Apenas administradores podem editar utilizadores.");
        }

        Optional<Utilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilizador utilizador = op.get();
        utilizador.setNome(dadosAtualizados.getNome());
        utilizador.setEmail(dadosAtualizados.getEmail());
        utilizador.setCargo(dadosAtualizados.getCargo());

        if (dadosAtualizados.getPassword() != null && !dadosAtualizados.getPassword().isBlank()) {
            utilizador.setPassword(dadosAtualizados.getPassword());
        }

        utilizadorRepository.save(utilizador);
        return ResponseEntity.ok(utilizador);
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        String token = extrairToken(authorization);

        if (!autorizacaoService.eAdmin(token)) {
            return ResponseEntity.status(403).body("Apenas administradores podem desativar utilizadores.");
        }

        Optional<Utilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilizador utilizador = op.get();
        utilizador.setAtivo(false);
        utilizadorRepository.save(utilizador);

        return ResponseEntity.ok("Utilizador desativado com sucesso.");
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        String token = extrairToken(authorization);

        if (!autorizacaoService.eAdmin(token)) {
            return ResponseEntity.status(403).body("Apenas administradores podem ativar utilizadores.");
        }

        Optional<Utilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilizador utilizador = op.get();
        utilizador.setAtivo(true);
        utilizador.setTentativasFalhadas(0);
        utilizadorRepository.save(utilizador);

        return ResponseEntity.ok("Utilizador ativado com sucesso.");
    }

    private String extrairToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }

        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return authorization;
    }
}