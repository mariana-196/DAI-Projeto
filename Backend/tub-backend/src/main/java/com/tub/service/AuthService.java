package com.tub.service;

import com.tub.adapter.AutenticacaoGovAdapter;
import com.tub.model.SessaoAutenticada;
import com.tub.model.Utilizador;
import com.tub.repository.SessaoAutenticadaRepository;
import com.tub.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;
    private final SessaoAutenticadaRepository sessaoAutenticadaRepository;
    private final AuditService auditService;
    private final AutenticacaoGovAdapter autenticacaoGovAdapter;

    public AuthService(
            UtilizadorRepository utilizadorRepository,
            SessaoAutenticadaRepository sessaoAutenticadaRepository,
            AuditService auditService,
            AutenticacaoGovAdapter autenticacaoGovAdapter
    ) {
        this.utilizadorRepository = utilizadorRepository;
        this.sessaoAutenticadaRepository = sessaoAutenticadaRepository;
        this.auditService = auditService;
        this.autenticacaoGovAdapter = autenticacaoGovAdapter;
    }

    public ResultadoAutenticacao autenticar(String email, String password) {
        Optional<Utilizador> op = utilizadorRepository.findByEmail(email);

        if (op.isEmpty()) {
            auditService.registar(
                    email,
                    "Falha de Autenticação",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Utilizador não encontrado"
            );
            return new ResultadoAutenticacao(false, "Credenciais inválidas ou conta desativada.", null, null);
        }

        Utilizador utilizador = op.get();

        if (!utilizador.isAtivo()) {
            auditService.registar(
                    email,
                    "Falha de Autenticação",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Utilizador inativo"
            );
            return new ResultadoAutenticacao(false, "Conta bloqueada por excesso de tentativas falhadas. Contacte o administrador.", null, null);
        }

        if (!utilizador.getPassword().equals(password)) {
            int tentativas = utilizador.getTentativasFalhadas() + 1;
            utilizador.setTentativasFalhadas(tentativas);

            if (tentativas >= 5) {
                utilizador.setAtivo(false);
                utilizadorRepository.save(utilizador);

                auditService.registar(
                        email,
                        "Conta Bloqueada",
                        "Autenticação",
                        "127.0.0.1",
                        "AVISO",
                        "Conta bloqueada por excesso de tentativas falhadas"
                );

                return new ResultadoAutenticacao(false, "Conta bloqueada por excesso de tentativas falhadas. Contacte o administrador.", null, null);
            }

            utilizadorRepository.save(utilizador);

            auditService.registar(
                    email,
                    "Falha de Autenticação",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Password incorreta. Tentativa " + tentativas + " de 5"
            );

            return new ResultadoAutenticacao(false, "Credenciais inválidas ou conta desativada.", null, null);
        }

        utilizador.setTentativasFalhadas(0);
        utilizadorRepository.save(utilizador);

        auditService.registar(
                email,
                "Início de Sessão",
                "Autenticação",
                "127.0.0.1",
                "INFO",
                "Login com sucesso"
        );

        return criarSessao(utilizador, "Login com sucesso.");
    }

    public ResultadoAutenticacao autenticarComGov(String email, String codigoGov) {
        boolean autenticadoGov = autenticacaoGovAdapter.autenticar(email, codigoGov);

        if (!autenticadoGov) {
            auditService.registar(
                    email,
                    "Falha de Autenticação Gov",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Falha na autenticação mockada via Autenticação.gov"
            );
            return new ResultadoAutenticacao(false, "Falha na autenticação via Autenticação.gov.", null, null);
        }

        Optional<Utilizador> op = utilizadorRepository.findByEmail(email);

        if (op.isEmpty()) {
            auditService.registar(
                    email,
                    "Falha de Autenticação Gov",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Utilizador autenticado externamente mas não existe na plataforma"
            );
            return new ResultadoAutenticacao(false, "Utilizador autenticado externamente mas não existe na plataforma.", null, null);
        }

        Utilizador utilizador = op.get();

        if (!utilizador.isAtivo()) {
            auditService.registar(
                    email,
                    "Falha de Autenticação Gov",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Utilizador inativo"
            );
            return new ResultadoAutenticacao(false, "Conta bloqueada por excesso de tentativas falhadas. Contacte o administrador.", null, null);
        }

        utilizador.setTentativasFalhadas(0);
        utilizadorRepository.save(utilizador);

        auditService.registar(
                email,
                "Início de Sessão Gov",
                "Autenticação",
                "127.0.0.1",
                "INFO",
                "Login mockado com Autenticação.gov"
        );

        return criarSessao(utilizador, "Login com Autenticação.gov realizado com sucesso.");
    }

    private ResultadoAutenticacao criarSessao(Utilizador utilizador, String mensagem) {
        String token = UUID.randomUUID().toString();

        SessaoAutenticada sessao = new SessaoAutenticada();
        sessao.setToken(token);
        sessao.setUtilizador(utilizador);
        sessao.setDataInicio(LocalDateTime.now());
        sessao.setDataExpiracao(LocalDateTime.now().plusHours(8));
        sessao.setAtiva(true);

        sessaoAutenticadaRepository.save(sessao);

        return new ResultadoAutenticacao(true, mensagem, token, utilizador);
    }

    public static class ResultadoAutenticacao {
        private final boolean sucesso;
        private final String mensagem;
        private final String token;
        private final Utilizador utilizador;

        public ResultadoAutenticacao(boolean sucesso, String mensagem, String token, Utilizador utilizador) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.token = token;
            this.utilizador = utilizador;
        }

        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public String getToken() { return token; }
        public Utilizador getUtilizador() { return utilizador; }
    }
}