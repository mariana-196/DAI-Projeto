
package com.tub.model;

public class ContextoElementoMapa {
    public String id;
    public String linha;
    public String estado;
    public double velocidade;
    public int lotacao;
    public String statusComunicacao;

    public ContextoElementoMapa(String id, String linha, String estado, double velocidade, int lotacao, String statusComunicacao) {
        this.id = id;
        this.linha = linha;
        this.estado = estado;
        this.velocidade = velocidade;
        this.lotacao = lotacao;
        this.statusComunicacao = statusComunicacao;
    }
}