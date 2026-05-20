package com.tub.config;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;

import com.tub.p2_dados_utilizador.model.RegistoUtilizador;
import com.tub.p2_dados_utilizador.repository.RegistoUtilizadorRepository;

import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;

import com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria;
import com.tub.p6_auditoria.model.RegistoAuditoria;
import com.tub.p6_auditoria.repository.PoliticasAuditoriaRepository;
import com.tub.p6_auditoria.repository.RegistoAuditoriaRepository;

import com.tub.p8_gestao_bilhetica.model.EstadoSincronizacao;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tub.p9_monitorizacao_iot.repository.LotacaoViaturaRepository;
import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;
import com.tub.p11_gestao_alertas.repository.AlertaLotacaoRepository;
import com.tub.p11_gestao_alertas.model.AlertaLotacao;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DadosDemonstracaoSeeder implements CommandLineRunner {

    private final RegistoUtilizadorRepository utilizadorRepository;
    private final LinhaRepository linhaRepository;
    private final ViaturasRepository viaturasRepository;
    private final DisplayPanelRepository displayPanelRepository;
    private final PoliticasAuditoriaRepository politicasAuditoriaRepository;
    private final RegistoAuditoriaRepository registoAuditoriaRepository;
    private final LoteDadosBilheticaRepository loteDadosBilheticaRepository;
    private final RegistoBilheticaRepository registoBilheticaRepository;
    private final LotacaoViaturaRepository lotacaoViaturaRepository;
    private final AlertaLotacaoRepository alertaLotacaoRepository;

    public DadosDemonstracaoSeeder(
            RegistoUtilizadorRepository utilizadorRepository,
            LinhaRepository linhaRepository,
            ViaturasRepository viaturasRepository,
            DisplayPanelRepository displayPanelRepository,
            PoliticasAuditoriaRepository politicasAuditoriaRepository,
            RegistoAuditoriaRepository registoAuditoriaRepository,
            LoteDadosBilheticaRepository loteDadosBilheticaRepository,
            RegistoBilheticaRepository registoBilheticaRepository,
            LotacaoViaturaRepository lotacaoViaturaRepository,
            AlertaLotacaoRepository alertaLotacaoRepository
    ) {
        this.utilizadorRepository = utilizadorRepository;
        this.linhaRepository = linhaRepository;
        this.viaturasRepository = viaturasRepository;
        this.displayPanelRepository = displayPanelRepository;
        this.politicasAuditoriaRepository = politicasAuditoriaRepository;
        this.registoAuditoriaRepository = registoAuditoriaRepository;
        this.loteDadosBilheticaRepository = loteDadosBilheticaRepository;
        this.registoBilheticaRepository = registoBilheticaRepository;
        this.lotacaoViaturaRepository = lotacaoViaturaRepository;
        this.alertaLotacaoRepository = alertaLotacaoRepository;
    }

    @Override
    public void run(String... args) {
        criarUtilizadores();
        criarPoliticasAuditoria();
        criarLogsAuditoria();
        criarLinhas();
        criarViaturas();
        criarPaineisPMD();
        criarDadosBilhetica();
        criarLotacaoEAlertas();

        System.out.println("Dados de demonstração M5 carregados com sucesso.");
    }

    private void criarUtilizadores() {
        if (utilizadorRepository.findByEmail("admin@tub.pt").isEmpty()) {
            RegistoUtilizador admin = new RegistoUtilizador(
                    "Administrador TUB",
                    "admin@tub.pt",
                    "1234",
                    "ADMINISTRADOR"
            );
            utilizadorRepository.save(admin);
        }

        if (utilizadorRepository.findByEmail("operador@tub.pt").isEmpty()) {
            RegistoUtilizador operador = new RegistoUtilizador(
                    "Operador TUB",
                    "operador@tub.pt",
                    "1234",
                    "OPERADOR"
            );
            utilizadorRepository.save(operador);
        }

        if (utilizadorRepository.findByEmail("bloqueado@tub.pt").isEmpty()) {
            RegistoUtilizador bloqueado = new RegistoUtilizador(
                    "Utilizador Bloqueado",
                    "bloqueado@tub.pt",
                    "1234",
                    "OPERADOR"
            );
            bloqueado.setAtivo(false);
            bloqueado.setTentativasFalhadas(5);
            utilizadorRepository.save(bloqueado);
        }
    }

    private void criarPoliticasAuditoria() {
        if (politicasAuditoriaRepository.count() == 0) {
            EntidadeConfiguracoesAuditoria politica = new EntidadeConfiguracoesAuditoria(
                    "INFO",
                    365,
                    true,
                    "admin@tub.pt"
            );
            politicasAuditoriaRepository.save(politica);
        }
    }

    private void criarLogsAuditoria() {
        if (registoAuditoriaRepository.count() == 0) {
            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "admin@tub.pt",
                    "Início de Sessão",
                    "Autenticação",
                    "127.0.0.1",
                    "INFO",
                    "Login com sucesso no protótipo de demonstração."
            ));

            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "operador@tub.pt",
                    "Consulta Dashboard",
                    "Dashboard Operacional",
                    "127.0.0.1",
                    "INFO",
                    "Consulta dos indicadores operacionais."
            ));

            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "bloqueado@tub.pt",
                    "Conta Bloqueada",
                    "Autenticação",
                    "127.0.0.1",
                    "AVISO",
                    "Conta bloqueada por excesso de tentativas falhadas."
            ));

            registoAuditoriaRepository.save(new RegistoAuditoria(
                    "sistema",
                    "Importação Bilhética",
                    "Bilhética",
                    "127.0.0.1",
                    "INFO",
                    "Importação simulada de dados de bilhética para demonstração M5."
            ));
        }
    }

    private void criarLinhas() {
        if (linhaRepository.count() == 0) {
            linhaRepository.save(criarLinha("43", "Linha 43 - Universidade / Estação", "Universidade do Minho", "Estação CP"));
            linhaRepository.save(criarLinha("2", "Linha 2 - Hospital / Centro", "Hospital de Braga", "Avenida Central"));
            linhaRepository.save(criarLinha("7", "Linha 7 - Bom Jesus / Centro", "Bom Jesus", "Arcada"));
            linhaRepository.save(criarLinha("24", "Linha 24 - Gualtar / Braga Parque", "Gualtar", "Braga Parque"));
            linhaRepository.save(criarLinha("15", "Linha 15 - Estação / Lamaçães", "Estação CP", "Lamaçães"));
        }
    }

    private Linha criarLinha(String codigo, String nome, String origem, String destino) {
        Linha linha = new Linha();
        linha.setCodigo(codigo);
        linha.setNome(nome);
        linha.setOrigem(origem);
        linha.setDestino(destino);
        linha.setAtiva(true);
        return linha;
    }

    private void criarViaturas() {
        if (viaturasRepository.count() == 0) {
            viaturasRepository.save(criarViatura(101, "AA-10-TB", "Mercedes Citaro", 80, true));
            viaturasRepository.save(criarViatura(102, "AA-11-TB", "MAN Lion's City", 75, true));
            viaturasRepository.save(criarViatura(103, "AA-12-TB", "Volvo 7900", 90, true));
            viaturasRepository.save(criarViatura(104, "AA-13-TB", "Caetano City Gold", 70, true));
            viaturasRepository.save(criarViatura(105, "AA-14-TB", "Mercedes eCitaro", 85, true));
            viaturasRepository.save(criarViatura(106, "AA-15-TB", "MAN Lion's City", 75, false));
        }
    }

    private Viatura criarViatura(Integer codigo, String matricula, String modelo, Integer capacidade, boolean ativa) {
        Viatura viatura = new Viatura();
        viatura.setCodigo(codigo);
        viatura.setMatricula(matricula);
        viatura.setModelo(modelo);
        viatura.setCapacidadeMaxima(capacidade);
        viatura.setAtiva(ativa);
        return viatura;
    }

    private void criarPaineisPMD() {
        if (displayPanelRepository.count() == 0) {
            displayPanelRepository.save(new DisplayPanel(
                    "PMD-001",
                    "Avenida Central",
                    "Linha 43 - próximo autocarro em 5 min",
                    "ONLINE",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-002",
                    "Universidade do Minho",
                    "Linha 24 - serviço normal",
                    "ONLINE",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-003",
                    "Hospital de Braga",
                    "Painel temporariamente indisponível",
                    "DEGRADADO",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-004",
                    "Estação CP",
                    "Linha 15 - próxima partida em 8 min",
                    "ONLINE",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-005",
                    "Bom Jesus",
                    "Informação operacional em atualização",
                    "OFFLINE",
                    LocalDateTime.now()
            ));
        }
    }

    private void criarDadosBilhetica() {
        if (registoBilheticaRepository.count() > 0) {
            return;
        }

        List<Linha> linhas = linhaRepository.findAll();
        List<Viatura> viaturas = viaturasRepository.findAll();

        if (linhas.isEmpty() || viaturas.isEmpty()) {
            return;
        }

        LoteDadosBilhetica lote = new LoteDadosBilhetica();
        lote.setCodigoLote("LOTE_DEMO_M5_" + System.currentTimeMillis());
        lote.setOrigem("BILHETICA_SIMULADA");
        lote.setEstado(EstadoSincronizacao.PROCESSADO);
        lote.setDataImportacao(LocalDateTime.now());
        lote = loteDadosBilheticaRepository.save(lote);

        Linha linha43 = procurarLinha(linhas, "43");
        Linha linha2 = procurarLinha(linhas, "2");
        Linha linha7 = procurarLinha(linhas, "7");
        Linha linha24 = procurarLinha(linhas, "24");
        Linha linha15 = procurarLinha(linhas, "15");

        Viatura v101 = viaturas.get(0);
        Viatura v102 = viaturas.get(1);
        Viatura v103 = viaturas.get(2);
        Viatura v104 = viaturas.get(3);
        Viatura v105 = viaturas.get(4);

        criarRegisto(lote, linha43, v101, "Gualtar - Universidade do Minho", "Passe Estudante", 35, "Gualtar", 3);
        criarRegisto(lote, linha43, v101, "Gualtar - Universidade do Minho", "Bilhete Normal", 28, "Gualtar", 2);
        criarRegisto(lote, linha43, v102, "Estação CP", "Passe Estudante", 22, "Centro", 1);
        criarRegisto(lote, linha43, v102, "Estação CP", "Bilhete Normal", 18, "Centro", 0);

        criarRegisto(lote, linha2, v103, "Hospital de Braga", "Passe Sénior", 26, "Hospital", 4);
        criarRegisto(lote, linha2, v103, "Hospital de Braga", "Bilhete Normal", 31, "Hospital", 3);
        criarRegisto(lote, linha2, v104, "Avenida Central", "Bilhete Normal", 44, "Centro", 2);
        criarRegisto(lote, linha2, v104, "Avenida Central", "Passe Estudante", 25, "Centro", 1);

        criarRegisto(lote, linha7, v105, "Bom Jesus", "Bilhete Normal", 18, "Bom Jesus", 5);
        criarRegisto(lote, linha7, v105, "Bom Jesus", "Passe Turístico", 20, "Bom Jesus", 4);
        criarRegisto(lote, linha7, v101, "Arcada", "Bilhete Normal", 30, "Centro", 3);
        criarRegisto(lote, linha7, v101, "Arcada", "Passe Sénior", 24, "Centro", 2);

        criarRegisto(lote, linha24, v102, "Braga Parque", "Passe Estudante", 41, "Braga Parque", 2);
        criarRegisto(lote, linha24, v102, "Braga Parque", "Bilhete Normal", 23, "Braga Parque", 1);
        criarRegisto(lote, linha24, v103, "Gualtar", "Passe Estudante", 19, "Gualtar", 0);

        criarRegisto(lote, linha15, v104, "Lamaçães", "Bilhete Normal", 16, "Lamaçães", 3);
        criarRegisto(lote, linha15, v104, "Lamaçães", "Passe Estudante", 12, "Lamaçães", 2);
        criarRegisto(lote, linha15, v105, "Estação CP", "Passe Sénior", 21, "Centro", 1);

        criarRegisto(lote, linha43, v101, "Paragem Desconhecida Norte", "Bilhete Normal", 9, "Norte", 0);
        criarRegisto(lote, linha2, v102, "Paragem Desconhecida Sul", "Bilhete Normal", 7, "Sul", 0);
    }

    private Linha procurarLinha(List<Linha> linhas, String codigo) {
        for (Linha linha : linhas) {
            if (linha.getCodigo().equals(codigo)) {
                return linha;
            }
        }
        return linhas.get(0);
    }

    private void criarRegisto(
            LoteDadosBilhetica lote,
            Linha linha,
            Viatura viatura,
            String paragem,
            String tipoTitulo,
            Integer validacoes,
            String zona,
            int horasAtras
    ) {
        RegistoBilhetica registo = new RegistoBilhetica();
        registo.setLote(lote);
        registo.setLinha(linha);
        registo.setViatura(viatura);
        registo.setDataHora(LocalDateTime.now().minusHours(horasAtras));
        registo.setParagemOrigem(paragem);
        registo.setTipoTitulo(tipoTitulo);
        registo.setValidacoes(validacoes);
        registo.setZona(zona);

        registoBilheticaRepository.save(registo);
    }

    private void criarLotacaoEAlertas() {
        if (lotacaoViaturaRepository.count() > 0) {
            return;
        }

        List<Viatura> viaturas = viaturasRepository.findAll();
        if (viaturas.isEmpty()) {
            return;
        }

        String[] linhasDemo = {"43", "2", "7", "15", "24", "2"};
        int[] passageirosAtuaisDemo = {28, 58, 12, 53, 4, 0};
        boolean[] sinalAtivoDemo = {true, true, true, true, true, false};

        for (int i = 0; i < viaturas.size(); i++) {
            Viatura v = viaturas.get(i);
            EstadoOcupacaoViatura lotacao = new EstadoOcupacaoViatura();
            lotacao.setViatura(v);
            lotacao.setLinha("Linha " + linhasDemo[i % linhasDemo.length]);
            lotacao.setPassageirosAtuais(passageirosAtuaisDemo[i % passageirosAtuaisDemo.length]);
            
            double cap = v.getCapacidadeMaxima() != null ? v.getCapacidadeMaxima() : 80;
            lotacao.setTaxaOcupacao((double) lotacao.getPassageirosAtuais() / cap * 100);
            lotacao.setSinalAtivo(sinalAtivoDemo[i % sinalAtivoDemo.length]);
            lotacao.setUltimaAtualizacao(LocalDateTime.now());
            lotacaoViaturaRepository.save(lotacao);

            if (lotacao.getTaxaOcupacao() >= 70.0) {
                AlertaLotacao alerta = new AlertaLotacao(
                    v,
                    lotacao.getLinha(),
                    "CRITICO",
                    "PENDENTE",
                    "Lotação Crítica - Ocupação atingiu " + String.format("%.1f", lotacao.getTaxaOcupacao()) + "% na " + lotacao.getLinha()
                );
                alertaLotacaoRepository.save(alerta);
            }
        }

        // Garantir alertas para os casos de uso específicos (Lotação, Painel DMS, Perda de Sinal GPS)
        // 1. Falha de painel DMS (associado à viatura 0 por conveniência de domínio)
        alertaLotacaoRepository.save(new AlertaLotacao(
            viaturas.get(0),
            "N/A",
            "CRITICO",
            "PENDENTE",
            "Falha de painel DMS - Painel #3 (Hospital de Braga) está offline e sem sinal de rede"
        ));

        // 2. Perda de sinal GPS (viatura 106, que está com sinal inativo no demo)
        alertaLotacaoRepository.save(new AlertaLotacao(
            viaturas.get(viaturas.size() - 1),
            "Linha 2",
            "CRITICO",
            "PENDENTE",
            "Perda de sinal GPS - Viatura #106 (" + viaturas.get(viaturas.size() - 1).getMatricula() + ") sem reporte de telemetria há mais de 15 minutos"
        ));

        // 3. Outro alerta histórico/em análise
        alertaLotacaoRepository.save(new AlertaLotacao(
            viaturas.get(1),
            "Linha 24",
            "MEDIA",
            "EM_TRATAMENTO",
            "Atraso reportado de 8 minutos devido a tráfego intenso na Avenida Central."
        ));
    }
}