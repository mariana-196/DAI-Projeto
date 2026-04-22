package com.tub.p4_gestao_utilizadores.controller;

import com.tub.p2_dados_utilizador.model.Utilizador;
import com.tub.p2_dados_utilizador.repository.UtilizadorRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilizadores")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ControladorAdministracaoContas {

    private final UtilizadorRepository utilizadorRepository;

    public ControladorAdministracaoContas(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @GetMapping
    public List<Utilizador> listar() {
        return utilizadorRepository.findAll();
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody Utilizador novoUser) {
        Optional<Utilizador> existente = utilizadorRepository.findByEmail(novoUser.getEmail());

        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("Este email já existe!");
        }

        utilizadorRepository.save(novoUser);
        return ResponseEntity.ok(novoUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Utilizador dadosAtualizados) {
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
    public ResponseEntity<?> desativar(@PathVariable Long id) {
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
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        Optional<Utilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilizador utilizador = op.get();
        utilizador.setAtivo(true);
        utilizadorRepository.save(utilizador);

        return ResponseEntity.ok("Utilizador ativado com sucesso.");
    }
}