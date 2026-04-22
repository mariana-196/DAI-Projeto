package com.tub.p6_auditoria.service;

import com.tub.p6_auditoria.model.RegistoAuditoria;
import com.tub.p6_auditoria.repository.RegistoAuditoriaRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ControloConsultaAuditoria {

    private final RegistoAuditoriaRepository registoAuditoriaRepository;

    public ControloConsultaAuditoria(RegistoAuditoriaRepository registoAuditoriaRepository) {
        this.registoAuditoriaRepository = registoAuditoriaRepository;
    }

    public List<RegistoAuditoria> pesquisarLogs(
            String utilizador,
            String acao,
            String modulo,
            String nivel,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return registoAuditoriaRepository.pesquisarComFiltros(
                utilizador, acao, modulo, nivel, dataInicio, dataFim
        );
    }

    public RegistoAuditoria guardarLog(RegistoAuditoria registo) {
        Objects.requireNonNull(registo, "O registo de auditoria não pode ser null");
        return registoAuditoriaRepository.save(registo);
    }

    public void registar(String utilizador, String acao, String modulo, String ipOrigem, String nivel, String detalhe) {
        RegistoAuditoria registo = new RegistoAuditoria(
                utilizador, acao, modulo, ipOrigem, nivel, detalhe
        );
        registoAuditoriaRepository.save(registo);
    }
}