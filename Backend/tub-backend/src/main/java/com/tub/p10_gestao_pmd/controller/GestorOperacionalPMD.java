package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibicao;
import com.tub.p10_gestao_pmd.repository.RepositorioTarefasExibicaoRepository;
import com.tub.p10_gestao_pmd.service.PainelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paineis")
@CrossOrigin(origins = "*") // Permite que o teu HTML fale com este Java
public class GestorOperacionalPMD {

    @Autowired
    private PainelService painelService;

    @Autowired
    private RepositorioTarefasExibicaoRepository tarefasExibicaoRepository;

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

    @PostMapping("/agendar")
    public ResponseEntity<?> agendarTarefa(@RequestBody Map<String, Object> payload) {
        try {
            Long mensagemId = Long.valueOf(payload.get("mensagemId").toString());
            String dataStr = payload.get("data").toString();
            String horaStr = payload.get("hora").toString();

            LocalDateTime dataHora = LocalDateTime.parse(dataStr + "T" + horaStr + ":00");

            RepositorioTarefasExibicao tarefa = new RepositorioTarefasExibicao();
            tarefa.setMensagemId(mensagemId);
            tarefa.setDataHoraExibicao(dataHora);
            tarefa.setConcluida(false);

            tarefasExibicaoRepository.save(tarefa);

            return ResponseEntity.ok("{\"status\": \"Sucesso\", \"mensagem\": \"Tarefa agendada com sucesso!\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }
}