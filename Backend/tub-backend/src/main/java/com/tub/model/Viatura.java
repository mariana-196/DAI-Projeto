package com.tub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "viaturas")
public class Viatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer codigo;

    @Column(unique = true)
    private String matricula;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Integer capacidadeMaxima;

    @Column(nullable = false)
    private boolean ativa = true;

    public Viatura() {}

    public Viatura(Integer codigo, String matricula, String modelo, Integer capacidadeMaxima, boolean ativa) {
        this.codigo = codigo;
        this.matricula = matricula;
        this.modelo = modelo;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativa = ativa;
    }

    public Long getId() { return id; }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(Integer capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
}
