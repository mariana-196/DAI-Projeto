package com.tub.repository;

import com.tub.model.Viatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViaturaRepository extends JpaRepository<Viatura, Long> {
    Optional<Viatura> findByCodigo(Integer codigo);
}