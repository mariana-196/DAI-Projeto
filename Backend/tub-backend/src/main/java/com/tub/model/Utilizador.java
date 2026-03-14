package com.tub.model;

public class Utilizador {
    private String nome;
    private String email;
    private String password;
    private String cargo;

    // Construtor vazio (obrigatório para o Java)
    public Utilizador() {}

    // Construtor com dados
    public Utilizador(String nome, String email, String password, String cargo) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.cargo = cargo;
    }

    // Getters e Setters (para o Java conseguir ler os dados)
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}