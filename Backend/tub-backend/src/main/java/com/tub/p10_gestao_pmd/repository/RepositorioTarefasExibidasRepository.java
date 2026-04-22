package com.tub.p10_gestao_pmd.repository;

import com.tub.p10_gestao_pmd.model.RepositorioTarefasExibidas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioTarefasExibidasRepository extends JpaRepository<RepositorioTarefasExibidas, Long> {
    

}