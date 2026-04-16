package com.tub.repository;

import com.tub.model.ModeloMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloMensagemRepository extends JpaRepository<ModeloMensagem, Long> {
}