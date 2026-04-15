package com.tub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modelos_mensagem")
public class ModeloMensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeModelo;

    @Column(nullable = false, length = 1000)
    private String conteudoModelo;

    @Column(nullable = false)
    private String categoria;

    public ModeloMensagem() {}

    public Long getId() { return id; }

    public String getNomeModelo() { return nomeModelo; }
    public void setNomeModelo(String nomeModelo) { this.nomeModelo = nomeModelo; }

    public String getConteudoModelo() { return conteudoModelo; }
    public void setConteudoModelo(String conteudoModelo) { this.conteudoModelo = conteudoModelo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}