package com.tub.repository;

import com.tub.model.MensagemPMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensagemPMDRepository extends JpaRepository<MensagemPMD, Long> {
}