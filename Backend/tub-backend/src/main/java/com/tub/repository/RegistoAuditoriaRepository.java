package com.tub.repository;

import com.tub.model.RegistoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistoAuditoriaRepository extends JpaRepository<RegistoAuditoria, Long> {

    @Query("""
        SELECT r
        FROM RegistoAuditoria r
        WHERE (:utilizador IS NULL OR LOWER(r.utilizador) LIKE LOWER(CONCAT('%', :utilizador, '%')))
          AND (:acao IS NULL OR LOWER(r.acao) LIKE LOWER(CONCAT('%', :acao, '%')))
          AND (:modulo IS NULL OR LOWER(r.modulo) LIKE LOWER(CONCAT('%', :modulo, '%')))
          AND (:nivel IS NULL OR LOWER(r.nivel) = LOWER(:nivel))
          AND (:dataInicio IS NULL OR r.timestamp >= :dataInicio)
          AND (:dataFim IS NULL OR r.timestamp <= :dataFim)
        ORDER BY r.timestamp DESC
    """)
    List<RegistoAuditoria> pesquisarComFiltros(
            @Param("utilizador") String utilizador,
            @Param("acao") String acao,
            @Param("modulo") String modulo,
            @Param("nivel") String nivel,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}