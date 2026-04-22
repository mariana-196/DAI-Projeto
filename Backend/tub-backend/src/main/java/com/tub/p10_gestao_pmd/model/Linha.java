package com.tub.p10_gestao_pmd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "linhas")
public class Linha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Linha() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
