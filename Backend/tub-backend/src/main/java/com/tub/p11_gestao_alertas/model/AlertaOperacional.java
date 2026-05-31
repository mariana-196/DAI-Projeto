package com.tub.p11_gestao_alertas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tub.p10_gestao_pmd.model.Viatura;

@Entity
@Table(name = "alertas_operacionais")
public class AlertaOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viatura_id", nullable = true)
    private Viatura viatura;

    @Column(nullable = true)
    private String linha;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tema; // LOTACAO, DMS, GPS, BILHETICA, VEHICLE_IOT, OPERATIONS

    @Column(nullable = false)
    private String severidade; // CRITICO, MEDIA, BAIXA

    @Column(nullable = false)
    private String estado; // PENDENTE, EM_TRATAMENTO, RESOLVIDO

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private String origem; // e.g. Wavecom IoT, Bilhética API, DMS CCO, GPS Telemetria

    @Column(nullable = true, length = 1000)
    private String infoAdicional; // Technical metadata (e.g. sensor values, stop panel code)

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alertas_operacionais_historico", joinColumns = @JoinColumn(name = "alerta_id"))
    @Column(name = "historico_log", length = 1000)
    private List<String> historico = new ArrayList<>();

    public AlertaOperacional() {}

    public AlertaOperacional(Viatura viatura, String linha, String titulo, String tema, String severidade, String estado, String descricao, String origem, String infoAdicional) {
        this.viatura = viatura;
        this.linha = linha;
        this.titulo = titulo;
        this.tema = tema;
        this.severidade = severidade;
        this.estado = estado;
        this.descricao = descricao;
        this.origem = origem;
        this.infoAdicional = infoAdicional;
        this.timestamp = LocalDateTime.now();
        this.historico.add(LocalDateTime.now().toString().replace("T", " ").substring(0, 16) + " - Alerta inicializado no sistema.");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Viatura getViatura() {
        return viatura;
    }

    public void setViatura(Viatura viatura) {
        this.viatura = viatura;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getSeveridade() {
        return severidade;
    }

    public void setSeveridade(String severidade) {
        this.severidade = severidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getHistorico() {
        return historico;
    }

    public void setHistorico(List<String> historico) {
        this.historico = historico;
    }

    public void adicionarLogHistorico(String acaoLog) {
        String dataHoraStr = LocalDateTime.now().toString().replace("T", " ").substring(0, 16);
        this.historico.add(dataHoraStr + " - " + acaoLog);
    }
}
