package com.tub.p1_autenticacao.controller;

import com.tub.p1_autenticacao.service.ControloSegurancaAutenticacao;
import com.tub.p2_dados_utilizador.controller.ControladorAcessos;
import com.tub.p2_dados_utilizador.controller.ControladorAcessos.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ControladorAcessosTest {

    @InjectMocks
    private ControladorAcessos controladorAcessos;

    @Mock
    private ControloSegurancaAutenticacao authService;

    @Test
    public void testarAutenticacaoComCredenciaisValidas() {
        String emailTeste = "admin@tub.pt";
        String passwordTeste = "senha123";

        LoginRequest requestSimulado = Mockito.mock(LoginRequest.class);
        Mockito.when(requestSimulado.getEmail()).thenReturn(emailTeste);
        Mockito.when(requestSimulado.getPassword()).thenReturn(passwordTeste);

        ControloSegurancaAutenticacao.ResultadoAutenticacao resultadoSucesso = 
            Mockito.mock(ControloSegurancaAutenticacao.ResultadoAutenticacao.class);
        Mockito.when(resultadoSucesso.isSucesso()).thenReturn(true);

        Mockito.when(authService.autenticar(emailTeste, passwordTeste)).thenReturn(resultadoSucesso);

        ResponseEntity<?> respostaAPI = controladorAcessos.login(requestSimulado);

        assertEquals(200, respostaAPI.getStatusCode().value());
        assertNotNull(respostaAPI.getBody());
        Mockito.verify(authService, Mockito.times(1)).autenticar(emailTeste, passwordTeste);
    }

    @Test
    public void testarBloqueioDeContaPorExcessoDeTentativas() {
        String emailTeste = "admin@tub.pt";
        String passwordErrada = "senhaErrada123";

        LoginRequest requestSimulado = Mockito.mock(LoginRequest.class);
        Mockito.when(requestSimulado.getEmail()).thenReturn(emailTeste);
        Mockito.when(requestSimulado.getPassword()).thenReturn(passwordErrada);

        ControloSegurancaAutenticacao.ResultadoAutenticacao resultadoFalha = 
            Mockito.mock(ControloSegurancaAutenticacao.ResultadoAutenticacao.class);
        Mockito.when(resultadoFalha.isSucesso()).thenReturn(false);

        ControloSegurancaAutenticacao.ResultadoAutenticacao resultadoBloqueado = 
            Mockito.mock(ControloSegurancaAutenticacao.ResultadoAutenticacao.class);
        Mockito.when(resultadoBloqueado.isSucesso()).thenReturn(false);

        Mockito.when(authService.autenticar(emailTeste, passwordErrada))
               .thenReturn(resultadoFalha, resultadoFalha, resultadoFalha, resultadoBloqueado);

        controladorAcessos.login(requestSimulado);
        controladorAcessos.login(requestSimulado);
        controladorAcessos.login(requestSimulado);
        ResponseEntity<?> respostaFinal = controladorAcessos.login(requestSimulado);

        assertEquals(401, respostaFinal.getStatusCode().value());
        Mockito.verify(authService, Mockito.times(4)).autenticar(emailTeste, passwordErrada);
    }
}   