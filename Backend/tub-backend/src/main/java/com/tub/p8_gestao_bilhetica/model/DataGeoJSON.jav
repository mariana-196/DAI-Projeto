package com.tub.p8_gestao_bilhetica.model;

public class DatasetGeoJSON {

    private String type;
    private String nome;
    private String conteudoGeoJson;

    public DatasetGeoJSON() {
    }

    public DatasetGeoJSON(String type, String nome, String conteudoGeoJson) {
        this.type = type;
        this.nome = nome;
        this.conteudoGeoJson = conteudoGeoJson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConteudoGeoJson() {
        return conteudoGeoJson;
    }

    public void setConteudoGeoJson(String conteudoGeoJson) {
        this.conteudoGeoJson = conteudoGeoJson;
    }
}