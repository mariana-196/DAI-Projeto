package com.tub.p6_auditoria.service;

import com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria;
import com.tub.p6_auditoria.repository.PoliticasAuditoriaRepository;
import com.tub.p6_auditoria.repository.RegistoAuditoriaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicoLimpezaAuditoria {

    private final RegistoAuditoriaRepository registoRepo;
    private final PoliticasAuditoriaRepository politicasRepo;

    public ServicoLimpezaAuditoria(RegistoAuditoriaRepository registoRepo, PoliticasAuditoriaRepository politicasRepo) {
        this.registoRepo = registoRepo;
        this.politicasRepo = politicasRepo;
    }

    // Configurei para correr de 1 em 1 minuto para poderes ver logo a funcionar na consola!
    // Nota: Para a entrega final, basta mudares o cron para "0 0 2 * * ?" (correr às 02:00 da manhã)
    @Scheduled(cron = "0 * * * * ?")
    public void limparLogsAutomaticamente() {
        List<EntidadeConfiguracoesAuditoria> politicas = politicasRepo.findAll();
        if (politicas.isEmpty()) return;

        // Vai buscar o número de dias guardado na base de dados (mínimo 365)
        int diasRetencao = politicas.get(0).getDiasRetencao();
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(diasRetencao);

        // Executa a remoção física dos logs antigos
        registoRepo.apagarLogsAntigos(dataLimite);
        System.out.println("LOG [Auditoria]: Limpeza automática executada. Logs anteriores a " + dataLimite + " foram eliminados.");
    }
}