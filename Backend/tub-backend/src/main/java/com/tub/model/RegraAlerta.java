package com.tub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "regras_alerta")
public class RegraAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Double limite;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false)
    private String descricao;

    public RegraAlerta() {}

    public RegraAlerta(String tipo, Double limite, boolean ativo, String descricao) {
        this.tipo = tipo;
        this.limite = limite;
        this.ativo = ativo;
        this.descricao = descricao;
    }

    public Long getId() { return id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getLimite() { return limite; }
    public void setLimite(Double limite) { this.limite = limite; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}