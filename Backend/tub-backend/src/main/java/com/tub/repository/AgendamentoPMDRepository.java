package com.tub.repository;

import com.tub.model.AgendamentoPMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendamentoPMDRepository extends JpaRepository<AgendamentoPMD, Long> {
}