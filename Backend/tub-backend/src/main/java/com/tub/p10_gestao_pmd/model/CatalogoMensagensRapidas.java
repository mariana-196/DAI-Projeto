package com.tub.p10_gestao_pmd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "catalogo_mensagens_rapidas")
public class CatalogoMensagensRapidas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeModelo;

    @Column(nullable = false, length = 1000)
    private String conteudoModelo;

    @Column(nullable = false)
    private String categoria;

    public CatalogoMensagensRapidas() {}

    public CatalogoMensagensRapidas(String nomeModelo, String conteudoModelo, String categoria) {
        this.nomeModelo = nomeModelo;
        this.conteudoModelo = conteudoModelo;
        this.categoria = categoria;
    }

    public Long getId() { return id; }

    public String getNomeModelo() { return nomeModelo; }
    public void setNomeModelo(String nomeModelo) { this.nomeModelo = nomeModelo; }

    public String getConteudoModelo() { return conteudoModelo; }
    public void setConteudoModelo(String conteudoModelo) { this.conteudoModelo = conteudoModelo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
