package com.tub.p10_gestao_pmd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tub.p10_gestao_pmd.model.PrevisaoChegada;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrevisaoChegadaRepository extends JpaRepository<PrevisaoChegada, Long> {
    
    List<PrevisaoChegada> findByPainelId(Long painelId);
    
    List<PrevisaoChegada> findByPainelIdAndTimestampAfter(Long painelId, LocalDateTime timestamp);
    
    @Transactional
    void deleteByPainelId(Long painelId);
    
    @Transactional
    void deleteByTimestampBefore(LocalDateTime timestamp);
}