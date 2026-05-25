package com.tub.p4_gestao_utilizadores.controller;

import com.tub.p2_dados_utilizador.model.RegistoUtilizador;
import com.tub.p2_dados_utilizador.repository.RegistoUtilizadorRepository;
import com.tub.p1_autenticacao.model.SessaoAutenticada;
import com.tub.p1_autenticacao.repository.SessaoAutenticadaRepository;
import com.tub.p1_autenticacao.annotation.RequerCargo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilizadores")
@CrossOrigin(origins = "*")
public class ControladorAdministracaoContas {

    private final RegistoUtilizadorRepository utilizadorRepository;
    private final SessaoAutenticadaRepository sessaoAutenticadaRepository;
    private final com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService;

    @Autowired
    private HttpServletRequest request;

    public ControladorAdministracaoContas(
            RegistoUtilizadorRepository utilizadorRepository,
            SessaoAutenticadaRepository sessaoAutenticadaRepository,
            com.tub.p6_auditoria.service.ControloConsultaAuditoria auditService) {
        this.utilizadorRepository = utilizadorRepository;
        this.sessaoAutenticadaRepository = sessaoAutenticadaRepository;
        this.auditService = auditService;
    }
    private boolean eAdmin(RegistoUtilizador utilizador) {
        if (utilizador == null) return false;
        String cargo = utilizador.getCargo();
        return "ADMINISTRADOR".equalsIgnoreCase(cargo);
    }

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    private RegistoUtilizador obterUtilizadorAutenticado() {
        Long userId = (Long) request.getAttribute("utilizador_id");
        if (userId == null) return null;
        return utilizadorRepository.findById(userId).orElse(null);
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado();
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
    @RequerCargo("ADMINISTRADOR")
    public ResponseEntity<?> guardar(@RequestBody RegistoUtilizador novoUser) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado();
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }

        if (emailInvalido(novoUser.getEmail())) {
            return ResponseEntity.badRequest().body("Erro: O formato do email é inválido!");
        }

        Optional<RegistoUtilizador> existente = utilizadorRepository.findByEmail(novoUser.getEmail());
        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("Este email já existe!");
        }

        utilizadorRepository.save(novoUser);
        auditService.registar(
                getExecutorEmail(),
                "CRIAR_UTILIZADOR",
                "Utilizadores",
                getExecutorIp(),
                "INFO",
                "Utilizador criado com sucesso: " + novoUser.getEmail() + " com cargo " + novoUser.getCargo()
        );
        return ResponseEntity.ok(novoUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody RegistoUtilizador dadosAtualizados) {
        RegistoUtilizador usuarioAutenticado = obterUtilizadorAutenticado();
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
        } else {
            utilizador.setNome(dadosAtualizados.getNome());
            utilizador.setEmail(dadosAtualizados.getEmail());
            utilizador.setCargo(dadosAtualizados.getCargo());
            utilizador.setAtivo(dadosAtualizados.isAtivo());
        }

        if (dadosAtualizados.getPassword() != null && !dadosAtualizados.getPassword().isBlank()) {
            utilizador.setPassword(dadosAtualizados.getPassword());
        }

        if (admin && emailInvalido(utilizador.getEmail())) {
            return ResponseEntity.badRequest().body("Erro: O formato do email é inválido!");
        }

        utilizadorRepository.save(utilizador);
        auditService.registar(
                getExecutorEmail(),
                "EDITAR_UTILIZADOR",
                "Utilizadores",
                getExecutorIp(),
                "INFO",
                "Utilizador editado com sucesso: " + utilizador.getEmail() + " (ID: " + id + ")"
        );
        return ResponseEntity.ok(utilizador);
    }

    @PutMapping("/{id}/desativar")
    @RequerCargo("ADMINISTRADOR")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);
        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistoUtilizador utilizador = op.get();
        utilizador.setAtivo(false);
        utilizadorRepository.save(utilizador);

        auditService.registar(
                getExecutorEmail(),
                "DESATIVAR_UTILIZADOR",
                "Utilizadores",
                getExecutorIp(),
                "INFO",
                "Utilizador desativado: " + utilizador.getEmail() + " (ID: " + id + ")"
        );
        return ResponseEntity.ok("Utilizador desativado com sucesso.");
    }

    @PutMapping("/{id}/ativar")
    @RequerCargo("ADMINISTRADOR")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);
        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistoUtilizador utilizador = op.get();
        utilizador.setAtivo(true);
        utilizadorRepository.save(utilizador);

        auditService.registar(
                getExecutorEmail(),
                "ATIVAR_UTILIZADOR",
                "Utilizadores",
                getExecutorIp(),
                "INFO",
                "Utilizador ativado: " + utilizador.getEmail() + " (ID: " + id + ")"
        );
        return ResponseEntity.ok("Utilizador ativado com sucesso.");
    }

    @DeleteMapping("/{id}")
    @RequerCargo("ADMINISTRADOR")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<RegistoUtilizador> op = utilizadorRepository.findById(id);
        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistoUtilizador utilizador = op.get();
        try {
            utilizadorRepository.deleteById(id);
            auditService.registar(
                    getExecutorEmail(),
                    "ELIMINAR_UTILIZADOR",
                    "Utilizadores",
                    getExecutorIp(),
                    "INFO",
                    "Utilizador eliminado permanentemente: " + utilizador.getEmail() + " (ID: " + id + ")"
            );
            return ResponseEntity.ok("Utilizador eliminado permanentemente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: Não é possível eliminar este utilizador pois ele possui registos históricos associados.");
        }
    }

    private boolean emailInvalido(String email) {
        if (email == null) return true;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return !email.matches(regex);
    }
}