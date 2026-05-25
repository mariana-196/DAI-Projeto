package com.tub.p4_gestao_utilizadores.controller;

import com.tub.p2_dados_utilizador.model.RegistoUtilizador;
import com.tub.p2_dados_utilizador.repository.RegistoUtilizadorRepository;
import com.tub.p1_autenticacao.model.SessaoAutenticada;
import com.tub.p1_autenticacao.repository.SessaoAutenticadaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilizadores")
@CrossOrigin(origins = "*")
public class ControladorAdministracaoContas {

    private final RegistoUtilizadorRepository utilizadorRepository;
    private final SessaoAutenticadaRepository sessaoAutenticadaRepository;

    public ControladorAdministracaoContas(
            RegistoUtilizadorRepository utilizadorRepository,
            SessaoAutenticadaRepository sessaoAutenticadaRepository) {
        this.utilizadorRepository = utilizadorRepository;
        this.sessaoAutenticadaRepository = sessaoAutenticadaRepository;
    }

    private RegistoUtilizador obterUtilizadorAutenticado(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7).trim();
        Optional<SessaoAutenticada> opSessao = sessaoAutenticadaRepository.findByToken(token);
        if (opSessao.isEmpty()) {
            return null;
        }
        SessaoAutenticada sessao = opSessao.get();
        if (!sessao.isAtiva()) {
            return null;
        }
        if (sessao.getDataExpiracao() != null && sessao.getDataExpiracao().isBefore(java.time.LocalDateTime.now())) {
            return null;
        }
        RegistoUtilizador utilizador = sessao.getUtilizador();
        if (utilizador == null || !utilizador.isAtivo()) {
            return null;
        }
        return utilizador;
    }

    private boolean eAdmin(RegistoUtilizador utilizador) {
        if (utilizador == null) return false;
        String cargo = utilizador.getCargo();
        return "ADMINISTRADOR".equalsIgnoreCase(cargo) || "ADMIN".equalsIgnoreCase(cargo);
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado(authHeader);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }

        if (eAdmin(usuarioAutenticado)) {
            return ResponseEntity.ok(utilizadorRepository.findAll());
        } else {
            return ResponseEntity.ok(List.of(usuarioAutenticado));
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody RegistoUtilizador novoUser) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado(authHeader);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }
        if (!eAdmin(usuarioAutenticado)) {
            return ResponseEntity.status(403).body("Apenas administradores podem criar novos utilizadores.");
        }

        // --- Validação de Formato de Email ---
        if (emailInvalido(novoUser.getEmail())) {
            return ResponseEntity.badRequest().body("Erro: O formato do email é inválido!");
        }
        // -------------------------------------------

        Optional<RegistoUtilizador> existente = utilizadorRepository.findByEmail(novoUser.getEmail());

        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("Este email já existe!");
        }

        utilizadorRepository.save(novoUser);
        return ResponseEntity.ok(novoUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody RegistoUtilizador dadosAtualizados) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado(authHeader);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }

        boolean admin = eAdmin(usuarioAutenticado);
        if (!admin && !usuarioAutenticado.getId().equals(id)) {
            return ResponseEntity.status(403).body("Não tem permissão para editar outros utilizadores.");
        }

        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistoUtilizador utilizador = op.get();
        if (!admin) {
            utilizador.setNome(dadosAtualizados.getNome());
            // Email, cargo, active status are ignored for non-admins to prevent spoofing
        } else {
            utilizador.setNome(dadosAtualizados.getNome());
            utilizador.setEmail(dadosAtualizados.getEmail());
            utilizador.setCargo(dadosAtualizados.getCargo());
            utilizador.setAtivo(dadosAtualizados.isAtivo());
        }

        if (dadosAtualizados.getPassword() != null && !dadosAtualizados.getPassword().isBlank()) {
            utilizador.setPassword(dadosAtualizados.getPassword());
        }

        // --- Validação de Formato de Email ---
        if (admin && emailInvalido(utilizador.getEmail())) {
            return ResponseEntity.badRequest().body("Erro: O formato do email é inválido!");
        }

        utilizadorRepository.save(utilizador);
        return ResponseEntity.ok(utilizador);
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado(authHeader);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }
        if (!eAdmin(usuarioAutenticado)) {
            return ResponseEntity.status(403).body("Apenas administradores podem desativar utilizadores.");
        }

        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistoUtilizador utilizador = op.get();
        utilizador.setAtivo(false);
        utilizadorRepository.save(utilizador);

        return ResponseEntity.ok("Utilizador desativado com sucesso.");
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado(authHeader);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }
        if (!eAdmin(usuarioAutenticado)) {
            return ResponseEntity.status(403).body("Apenas administradores podem ativar utilizadores.");
        }

        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistoUtilizador utilizador = op.get();
        utilizador.setAtivo(true);
        utilizadorRepository.save(utilizador);

        return ResponseEntity.ok("Utilizador ativado com sucesso.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado(authHeader);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }
        if (!eAdmin(usuarioAutenticado)) {
            return ResponseEntity.status(403).body("Apenas administradores podem eliminar utilizadores.");
        }

        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);

        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            utilizadorRepository.deleteById(id);
            return ResponseEntity.ok("Utilizador eliminado permanentemente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: Não é possível eliminar este utilizador pois ele possui registos históricos associados.");
        }
    }

    // --- NOVO: Função Auxiliar de Validação ---
    private boolean emailInvalido(String email) {
        if (email == null) return true;
        // Verifica se tem texto + @ + texto + . + extensão (mínimo 2 letras)
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return !email.matches(regex);
    }
}