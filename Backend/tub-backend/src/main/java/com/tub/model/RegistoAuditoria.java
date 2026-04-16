package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registos_auditoria")
public class RegistoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private String utilizador;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private String modulo;

    @Column(nullable = false)
    private String ipOrigem;

    @Column(nullable = false)
    private String nivel;

    @Column(length = 1000)
    private String detalhe;

    public RegistoAuditoria() {}

    public RegistoAuditoria(String utilizador, String acao, String modulo, String ipOrigem, String nivel, String detalhe) {
        this.utilizador = utilizador;
        this.acao = acao;
        this.modulo = modulo;
        this.ipOrigem = ipOrigem;
        this.nivel = nivel;
        this.detalhe = detalhe;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String getUtilizador() { return utilizador; }
    public void setUtilizador(String utilizador) { this.utilizador = utilizador; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }
}