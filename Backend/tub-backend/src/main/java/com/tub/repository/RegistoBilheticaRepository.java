package com.tub.repository;

import com.tub.model.RegistoBilhetica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistoBilheticaRepository extends JpaRepository<RegistoBilhetica, Long> {
}