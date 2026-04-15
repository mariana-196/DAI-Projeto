package com.tub.repository;

import com.tub.model.EventoPMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoPMDRepository extends JpaRepository<EventoPMD, Long> {
}