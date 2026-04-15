package com.tub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "publicacoes_pmd")
public class PublicacaoPMD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "painel_id", nullable = false)
    private PainelPMD painel;

    @ManyToOne
    @JoinColumn(name = "mensagem_id", nullable = false)
    private MensagemPMD mensagem;

    @Column(nullable = false)
    private String estadoPublicacao; // ENVIADA, ATIVA, REMOVIDA, FALHOU

    @Column(nullable = false)
    private LocalDateTime dataEnvio = LocalDateTime.now();

    private LocalDateTime dataRemocao;

    @Column(length = 1000)
    private String observacoes;

    public PublicacaoPMD() {}

    public Long getId() { return id; }

    public PainelPMD getPainel() { return painel; }
    public void setPainel(PainelPMD painel) { this.painel = painel; }

    public MensagemPMD getMensagem() { return mensagem; }
    public void setMensagem(MensagemPMD mensagem) { this.mensagem = mensagem; }

    public String getEstadoPublicacao() { return estadoPublicacao; }
    public void setEstadoPublicacao(String estadoPublicacao) { this.estadoPublicacao = estadoPublicacao; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }

    public LocalDateTime getDataRemocao() { return dataRemocao; }
    public void setDataRemocao(LocalDateTime dataRemocao) { this.dataRemocao = dataRemocao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
