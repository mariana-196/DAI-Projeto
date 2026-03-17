package com.tub.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuditoriaController {

    @GetMapping("/logs")
    public List<Map<String, String>> getLogs() {
        List<Map<String, String>> logs = new ArrayList<>();

        // Dados para a tua Auditoria bombar no browser
        logs.add(createLog("12/03/2026 08:30:12", "admin@tub.pt", "Início de Sessão", "Autenticação", "192.168.1.45", "INFO"));
        logs.add(createLog("12/03/2026 09:15:00", "operador2@tub.pt", "Falha de Autenticação", "Autenticação", "85.240.12.5", "AVISO"));
        logs.add(createLog("13/03/2026 10:05:40", "admin@tub.pt", "Exportação (CSV)", "Bilhética", "192.168.1.45", "CRÍTICO"));

        return logs;
    }

    private Map<String, String> createLog(String data, String user, String acao, String modulo, String ip, String nivel) {
        Map<String, String> log = new HashMap<>();
        log.put("data", data);
        log.put("utilizador", user);
        log.put("acao", acao);
        log.put("modulo", modulo);
        log.put("ip", ip);
        log.put("nivel", nivel);
        return log;
    }
}