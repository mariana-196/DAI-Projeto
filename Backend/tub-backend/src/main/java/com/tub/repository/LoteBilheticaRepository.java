package com.tub.repository;

import com.tub.model.LoteBilhetica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteBilheticaRepository extends JpaRepository<LoteBilhetica, Long> {
}