package com.tub.p10_gestao_pmd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "viaturas")
public class Viatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Viatura() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}