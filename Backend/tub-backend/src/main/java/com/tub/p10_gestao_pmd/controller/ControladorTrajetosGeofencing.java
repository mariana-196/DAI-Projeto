package com.tub.p10_gestao_pmd.controller;

import com.tub.p10_gestao_pmd.model.EventoGeografico;
import com.tub.p10_gestao_pmd.repository.EventoGeograficoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.tub.p1_autenticacao.annotation.RequerCargo;
import com.tub.p6_auditoria.service.ControloConsultaAuditoria;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador responsável por registar as entradas e saídas 
 * das viaturas nas zonas de controlo (Geofences).
 */
@RestController
@RequestMapping("/api/geofencing")
@CrossOrigin(origins = "*")
@RequerCargo({"OPERADOR", "ADMINISTRADOR"})
public class ControladorTrajetosGeofencing {

    @Autowired
    private EventoGeograficoRepository eventoRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ControloConsultaAuditoria auditService;

    private String getExecutorEmail() {
        String email = (String) request.getAttribute("utilizador_email");
        return email != null ? email : "Dispositivo/Sistema";
    }

    private String getExecutorIp() {
        return request.getRemoteAddr();
    }

    /**
     * Regista quando um autocarro entra ou sai de uma zona no mapa
     */
    @PostMapping("/registar")
    public ResponseEntity<EventoGeografico> registarEvento(
            @RequestParam Long viaturaId,
            @RequestParam String nomeZona,
            @RequestParam String tipoMovimento) { // Pode ser "ENTRADA" ou "SAIDA"

        EventoGeografico novoEvento = new EventoGeografico();
        novoEvento.setViaturaId(viaturaId);
        
        // Usar os nomes reais que tu criaste no Model!
        novoEvento.setDetalhes(nomeZona); 
        novoEvento.setTipo(tipoMovimento); 
        novoEvento.setTimestamp(LocalDateTime.now());

        EventoGeografico eventoGuardado = eventoRepository.save(novoEvento);

        try {
            auditService.registar(
                    getExecutorEmail(),
                    "EVENTO_GEOFENCING",
                    "Frota",
                    getExecutorIp(),
                    "INFO",
                    "Movimento geográfico registado para viatura #" + viaturaId + ": " + tipoMovimento + " na zona " + nomeZona
            );
        } catch (Exception e) {
            System.err.println("Erro ao registar auditoria: " + e.getMessage());
        }

        return ResponseEntity.ok(eventoGuardado);
    }

    /**
     * Devolve o histórico de eventos para a barra lateral do teu HTML
     */
    @GetMapping("/historico")
    public ResponseEntity<List<EventoGeografico>> obterHistoricoEventos() {
        List<EventoGeografico> historico = eventoRepository.findAll();
        return ResponseEntity.ok(historico);
    }
}
