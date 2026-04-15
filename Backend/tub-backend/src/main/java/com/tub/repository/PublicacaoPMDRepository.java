package com.tub.repository;

import com.tub.model.PublicacaoPMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacaoPMDRepository extends JpaRepository<PublicacaoPMD, Long> {
}