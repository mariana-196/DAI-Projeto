package com.tub.controller;

import com.tub.model.DisplayPanel;
import com.tub.service.PainelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paineis")
@CrossOrigin(origins = "*") // Permite que o teu HTML fale com este Java
public class PainelController {

    @Autowired
    private PainelService painelService;

    /**
     * Linha 94: Responde ao pedido GET para listar o estado atual dos painéis.
     * Agora os dados vêm da Base de Dados através do Service.
     */
    @GetMapping
    public ResponseEntity<List<DisplayPanel>> listarPaineis() {
        List<DisplayPanel> lista = painelService.listarTodosOsPaineis();
        return ResponseEntity.ok(lista);
    }

    /**
     * Linha 99: Controller para o Histórico de Mensagens.
     * Devolve a lista de mensagens para o ecrã de histórico (Linha 98).
     */
    @GetMapping("/historico")
    public ResponseEntity<List<DisplayPanel>> consultarHistorico() {
        // Por agora, usamos a mesma lógica de listar, mas aqui podes filtrar
        // no futuro se quiseres apenas mensagens enviadas por administradores.
        List<DisplayPanel> historico = painelService.listarTodosOsPaineis();
        return ResponseEntity.ok(historico);
    }

    /**
     * Linha 94: Responde ao pedido POST para publicar novas mensagens.
     * Utiliza a lógica de negócio do Service (Linha 95/100).
     */
    @PostMapping("/publicar")
    public ResponseEntity<String> publicarMensagem(@RequestBody DisplayPanel dadosRecebidos) {
        try {
            String destino = dadosRecebidos.getPanelId();
            String novaMensagem = dadosRecebidos.getMessage();

            // Se o destino for "TODOS", fazemos Broadcast
            if ("TODOS".equals(destino)) {
                painelService.publicarMensagemBroadcast(novaMensagem);
            } 
            // Caso contrário, enviamos para um painel específico
            else {
                painelService.publicarMensagemNumPainel(destino, novaMensagem);
            }
            
            return ResponseEntity.ok("{\"resultado\": \"Mensagem publicada e guardada na BD com sucesso!\"}");

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Caso o painel esteja degradado ou não exista
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"erro\": \"Erro ao processar o pedido no servidor.\"}");
        }
    }
}