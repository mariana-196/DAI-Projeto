package com.tub.config;

import com.tub.p10_gestao_pmd.model.DisplayPanel;
import com.tub.p10_gestao_pmd.model.Linha;
import com.tub.p10_gestao_pmd.model.Viatura;
import com.tub.p10_gestao_pmd.model.PainelPMD;
import com.tub.p10_gestao_pmd.model.CatalogoMensagensRapidas;
import com.tub.p10_gestao_pmd.repository.DisplayPanelRepository;
import com.tub.p10_gestao_pmd.repository.PainelPMDRepository;
import com.tub.p10_gestao_pmd.repository.CatalogoMensagensRapidasRepository;

import com.tub.p1_autenticacao.model.RegistoUtilizador;
import com.tub.p1_autenticacao.repository.RegistoUtilizadorRepository;

import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p5_lotacao.repository.ViaturasRepository;

import com.tub.p6_auditoria.model.EntidadeConfiguracoesAuditoria;
import com.tub.p6_auditoria.model.RegistoAuditoria;
import com.tub.p6_auditoria.repository.PoliticasAuditoriaRepository;
import com.tub.p6_auditoria.repository.RegistoAuditoriaRepository;
import com.tub.p5_lotacao.model.HistoricoLotacao;
import com.tub.p5_lotacao.repository.HistoricoLotacaoRepository;

import com.tub.p8_gestao_bilhetica.model.ConfiguracaoIntegracao;
import com.tub.p8_gestao_bilhetica.model.EstadoSincronizacao;
import com.tub.p8_gestao_bilhetica.model.LoteDadosBilhetica;
import com.tub.p8_gestao_bilhetica.model.RegistoBilhetica;
import com.tub.p8_gestao_bilhetica.repository.ConfiguracaoIntegracaoRepository;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tub.p9_monitorizacao_iot.repository.LotacaoViaturaRepository;
import com.tub.p9_monitorizacao_iot.model.EstadoOcupacaoViatura;
import com.tub.p11_gestao_alertas.repository.AlertaOperacionalRepository;
import com.tub.p11_gestao_alertas.model.AlertaOperacional;
import com.tub.p12_kpis_operacionais.repository.RegistoPontualidadeRepository;
import com.tub.p12_kpis_operacionais.model.RegistoPontualidade;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DadosDemonstracaoSeeder implements CommandLineRunner {

    private final RegistoUtilizadorRepository utilizadorRepository;
    private final LinhaRepository linhaRepository;
    private final ViaturasRepository viaturasRepository;
    private final DisplayPanelRepository displayPanelRepository;
    private final PainelPMDRepository painelPMDRepository;
    private final PoliticasAuditoriaRepository politicasAuditoriaRepository;
    private final RegistoAuditoriaRepository registoAuditoriaRepository;
    private final LoteDadosBilheticaRepository loteDadosBilheticaRepository;
    private final RegistoBilheticaRepository registoBilheticaRepository;
    private final LotacaoViaturaRepository lotacaoViaturaRepository;
    private final AlertaOperacionalRepository alertaOperacionalRepository;
    private final ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository;
    private final CatalogoMensagensRapidasRepository catalogoMensagensRapidasRepository;
    private final RegistoPontualidadeRepository registoPontualidadeRepository;
    private final HistoricoLotacaoRepository historicoLotacaoRepository;

    public DadosDemonstracaoSeeder(
            RegistoUtilizadorRepository utilizadorRepository,
            LinhaRepository linhaRepository,
            ViaturasRepository viaturasRepository,
            DisplayPanelRepository displayPanelRepository,
            PainelPMDRepository painelPMDRepository,
            PoliticasAuditoriaRepository politicasAuditoriaRepository,
            RegistoAuditoriaRepository registoAuditoriaRepository,
            LoteDadosBilheticaRepository loteDadosBilheticaRepository,
            RegistoBilheticaRepository registoBilheticaRepository,
            LotacaoViaturaRepository lotacaoViaturaRepository,
            AlertaOperacionalRepository alertaOperacionalRepository,
            ConfiguracaoIntegracaoRepository configuracaoIntegracaoRepository,
            CatalogoMensagensRapidasRepository catalogoMensagensRapidasRepository,
            RegistoPontualidadeRepository registoPontualidadeRepository,
            HistoricoLotacaoRepository historicoLotacaoRepository
    ) {
        this.utilizadorRepository = utilizadorRepository;
        this.linhaRepository = linhaRepository;
        this.viaturasRepository = viaturasRepository;
        this.displayPanelRepository = displayPanelRepository;
        this.painelPMDRepository = painelPMDRepository;
        this.politicasAuditoriaRepository = politicasAuditoriaRepository;
        this.registoAuditoriaRepository = registoAuditoriaRepository;
        this.loteDadosBilheticaRepository = loteDadosBilheticaRepository;
        this.registoBilheticaRepository = registoBilheticaRepository;
        this.lotacaoViaturaRepository = lotacaoViaturaRepository;
        this.alertaOperacionalRepository = alertaOperacionalRepository;
        this.configuracaoIntegracaoRepository = configuracaoIntegracaoRepository;
        this.catalogoMensagensRapidasRepository = catalogoMensagensRapidasRepository;
        this.registoPontualidadeRepository = registoPontualidadeRepository;
        this.historicoLotacaoRepository = historicoLotacaoRepository;
    }

    @Override
    public void run(String... args) {
        criarUtilizadores();
        criarPoliticasAuditoria();
        criarLogsAuditoria();
        criarLinhas();
        criarViaturas();
        criarPaineisPMD();
        criarLotacaoEAlertas();
        criarConfiguracaoIntegracao();
        criarCatalogoMensagensRapidas();
        criarDadosBilhetica();
        criarRegistoPontualidade();

        System.out.println("Dados de demonstração carregados com sucesso.");
    }

    private void criarRegistoPontualidade() {
        if (registoPontualidadeRepository.count() == 0) {
            registoPontualidadeRepository.save(new RegistoPontualidade("43", "BUS-101", 86));
            registoPontualidadeRepository.save(new RegistoPontualidade("43", "BUS-102", 75));
            registoPontualidadeRepository.save(new RegistoPontualidade("12", "BUS-102", 95));
            registoPontualidadeRepository.save(new RegistoPontualidade("7", "BUS-103", 60));
            registoPontualidadeRepository.save(new RegistoPontualidade("7", "BUS-105", 55));
            registoPontualidadeRepository.save(new RegistoPontualidade("2", "BUS-104", 100));
            registoPontualidadeRepository.save(new RegistoPontualidade("24", "BUS-102", 82));
            registoPontualidadeRepository.save(new RegistoPontualidade("24", "BUS-103", 88));
            registoPontualidadeRepository.save(new RegistoPontualidade("15", "BUS-104", 92));
            registoPontualidadeRepository.save(new RegistoPontualidade("15", "BUS-105", 96));
        }
    }

    private void criarUtilizadores() {
        utilizadorRepository.findByEmail("admin@tub.pt").ifPresentOrElse(
                admin -> {
                    admin.setNome("Administrador TUB");
                    admin.setPassword("1234");
                    admin.setCargo("ADMINISTRADOR");
                    admin.setAtivo(true);
                    admin.setTentativasFalhadas(0);
                    utilizadorRepository.save(admin);
                },
                () -> {
                    RegistoUtilizador admin = new RegistoUtilizador(
                            "Administrador TUB",
                            "admin@tub.pt",
                            "1234",
                            "ADMINISTRADOR"
                    );
                    utilizadorRepository.save(admin);
                }
        );

        utilizadorRepository.findByEmail("operador@tub.pt").ifPresentOrElse(
                operador -> {
                    operador.setNome("Operador TUB");
                    operador.setPassword("1234");
                    operador.setCargo("OPERADOR");
                    operador.setAtivo(true);
                    operador.setTentativasFalhadas(0);
                    utilizadorRepository.save(operador);
                },
                () -> {
                    RegistoUtilizador operador = new RegistoUtilizador(
                            "Operador TUB",
                            "operador@tub.pt",
                            "1234",
                            "OPERADOR"
                    );
                    utilizadorRepository.save(operador);
                }
        );

        utilizadorRepository.findByEmail("bloqueado@tub.pt").ifPresentOrElse(
                bloqueado -> {
                    bloqueado.setNome("Utilizador Bloqueado");
                    bloqueado.setPassword("1234");
                    bloqueado.setCargo("OPERADOR");
                    bloqueado.setAtivo(false);
                    bloqueado.setTentativasFalhadas(5);
                    utilizadorRepository.save(bloqueado);
                },
                () -> {
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
        );
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
                    "MEDIA",
                    "ONLINE",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-002",
                    "Universidade do Minho",
                    "Linha 24 - serviço normal",
                    "MEDIA",
                    "ONLINE",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-003",
                    "Hospital de Braga",
                    "Painel temporariamente indisponível",
                    null,
                    "DEGRADADO",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-004",
                    "Estação CP",
                    "Linha 15 - próxima partida em 8 min",
                    "MEDIA",
                    "ONLINE",
                    LocalDateTime.now()
            ));

            displayPanelRepository.save(new DisplayPanel(
                    "PMD-005",
                    "Bom Jesus",
                    "Informação operacional em atualização",
                    null,
                    "OFFLINE",
                    LocalDateTime.now()
            ));
        }

        if (painelPMDRepository.count() == 0) {
            painelPMDRepository.save(criarPainelPMDEntidade("PMD-001", "Avenida Central", "Avenida Central", "Centro", 41.5518, -8.4229, "ONLINE"));
            painelPMDRepository.save(criarPainelPMDEntidade("PMD-002", "Universidade do Minho", "Gualtar", "Gualtar", 41.5612, -8.3978, "ONLINE"));
            painelPMDRepository.save(criarPainelPMDEntidade("PMD-003", "Hospital de Braga", "Hospital", "Hospital", 41.5683, -8.3995, "DEGRADADO"));
            painelPMDRepository.save(criarPainelPMDEntidade("PMD-004", "Estação CP", "Estação CP", "Estação", 41.5492, -8.4344, "ONLINE"));
            painelPMDRepository.save(criarPainelPMDEntidade("PMD-005", "Bom Jesus", "Bom Jesus", "Tenões", 41.5546, -8.3775, "OFFLINE"));
        }
    }

    private PainelPMD criarPainelPMDEntidade(String codigo, String nome, String localizacao, String zona, Double lat, Double lng, String estado) {
        PainelPMD p = new PainelPMD();
        p.setCodigo(codigo);
        p.setNome(nome);
        p.setLocalizacao(localizacao);
        p.setZona(zona);
        p.setLatitude(lat);
        p.setLongitude(lng);
        p.setEstado(estado);
        p.setAtivo(true);
        p.setUltimaAtualizacao(LocalDateTime.now());
        return p;
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
        criarRegisto(lote, linha7, v101, "Arcada", "Bilhete Normal", 45, "Centro", 3);
        criarRegisto(lote, linha7, v101, "Arcada", "Passe Sénior", 35, "Centro", 2);

        criarRegisto(lote, linha24, v102, "Braga Parque", "Passe Estudante", 41, "Braga Parque", 2);
        criarRegisto(lote, linha24, v102, "Braga Parque", "Bilhete Normal", 23, "Braga Parque", 1);
        criarRegisto(lote, linha24, v103, "Gualtar", "Passe Estudante", 19, "Gualtar", 0);

        criarRegisto(lote, linha15, v104, "Lamaçães", "Bilhete Normal", 45, "Lamaçães", 3);
        criarRegisto(lote, linha15, v104, "Lamaçães", "Passe Estudante", 32, "Lamaçães", 2);
        criarRegisto(lote, linha15, v105, "Estação CP", "Passe Sénior", 21, "Centro", 1);

        criarRegisto(lote, linha43, v101, "Paragem Desconhecida Norte", "Bilhete Normal", 15, "Norte", 0);
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
        int[] passageirosAtuaisDemo = {75, 58, 5, 65, 4, 0};
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

            if (lotacao.getTaxaOcupacao() >= 80.0) {
                AlertaOperacional alerta = new AlertaOperacional(
                    v,
                    lotacao.getLinha(),
                    "Lotação Crítica (IoT)",
                    "LOTACAO",
                    "CRITICO",
                    "PENDENTE",
                    "Ocupação atingiu " + String.format("%.1f", lotacao.getTaxaOcupacao()) + "% na " + lotacao.getLinha() + " (Viatura #" + v.getCodigo() + ")",
                    "IoT Sensores",
                    "Sensor ID: SEN-OCC-" + v.getCodigo() + ", Limite: 80%, Passageiros: " + lotacao.getPassageirosAtuais() + "/" + (int)cap
                );
                alertaOperacionalRepository.save(alerta);
            }

            // Seed HistoricoLotacao
            com.tub.p5_lotacao.model.Viatura p5Viatura = new com.tub.p5_lotacao.model.Viatura();
            p5Viatura.setId(v.getId());
            p5Viatura.setCodigo(v.getCodigo());

            HistoricoLotacao h1 = new HistoricoLotacao();
            h1.setViatura(p5Viatura);
            h1.setVariacao(10);
            h1.setPassageirosResultantes(lotacao.getPassageirosAtuais() > 10 ? lotacao.getPassageirosAtuais() - 10 : lotacao.getPassageirosAtuais());
            h1.setTipoEvento("ENTRADA");
            historicoLotacaoRepository.save(h1);

            HistoricoLotacao h2 = new HistoricoLotacao();
            h2.setViatura(p5Viatura);
            h2.setVariacao(5);
            h2.setPassageirosResultantes(lotacao.getPassageirosAtuais());
            h2.setTipoEvento("ENTRADA");
            historicoLotacaoRepository.save(h2);
        }

        // Seeding de Alertas Operacionais de Alta Fidelidade (6 cenários realistas para Braga)
        
        // 1. Falha de painel DMS (Hospital de Braga)
        AlertaOperacional dmsAlerta = new AlertaOperacional(
            null, // Não associado a nenhuma viatura específica
            "N/A",
            "Falha de Painel DMS",
            "DMS",
            "CRITICO",
            "PENDENTE",
            "Painel PMD-003 (Hospital de Braga) offline. Sem reporte de sinal celular ou resposta a ping remoto há mais de 30 minutos.",
            "DMS Matriz",
            "Painel ID: PMD-003, Localização: Hospital de Braga, IP: 10.12.188.103, Sinal celular: 0%"
        );
        alertaOperacionalRepository.save(dmsAlerta);

        // 2. Perda de sinal GPS / Rastreamento (Viatura #106)
        AlertaOperacional gpsAlerta = new AlertaOperacional(
            viaturas.get(viaturas.size() - 1), // Viatura #106
            "Linha 2",
            "Perda de Sinal GPS",
            "GPS",
            "CRITICO",
            "PENDENTE",
            "Perda de sinal de telemetria da Viatura #106 sem reporte de telemetria há mais de 15 minutos.",
            "GPS Telemetria",
            "Viatura ID: 106, Matrícula: " + viaturas.get(viaturas.size() - 1).getMatricula() + ", Última Telemetria: 15 minutos atrás no Hospital de Braga"
        );
        alertaOperacionalRepository.save(gpsAlerta);

        // 3. Anomalia de Bilhética (Validador #12 na Viatura #101)
        AlertaOperacional bilheticaAlerta = new AlertaOperacional(
            viaturas.get(0), // Viatura #101
            "Linha 43",
            "Falha de Comunicação Validador",
            "BILHETICA",
            "MEDIA",
            "PENDENTE",
            "Validador #12 na viatura #101 está impedido de sincronizar as transações da tarde. Erro interno: GATEWAY_TIMEOUT.",
            "Bilhética API",
            "Validador ID: VAL-12, Viatura: #101, Transações em cache: 187, Firmware: v3.2.1-prod"
        );
        alertaOperacionalRepository.save(bilheticaAlerta);

        // 4. Mecânico / Temperatura IoT (Viatura elétrica #105)
        AlertaOperacional iotAlerta = new AlertaOperacional(
            viaturas.get(4), // Viatura #105
            "Linha 15",
            "Temperatura Elevada Baterias",
            "VEHICLE_IOT",
            "CRITICO",
            "PENDENTE",
            "Alerta Crítico de Temperatura do Pack de Baterias. Viatura elétrica #105 com temperatura do bloco 3 acima de 98°C. Procedimento preventivo aconselhado.",
            "Wavecom IoT",
            "Viatura: #105 (eCitaro), Sensor: TEMP_BAT_B3, Leitura: 98.4°C, Limite Máx: 80.0°C"
        );
        alertaOperacionalRepository.save(iotAlerta);

        // 5. Atraso Severo e Bloqueio de Via (Linha 7 - Arcada)
        AlertaOperacional delayAlerta = new AlertaOperacional(
            viaturas.get(1), // Viatura #102
            "Linha 24", // Linha 24
            "Atraso Severo Operacional",
            "OPERATIONS",
            "MEDIA",
            "EM_TRATAMENTO",
            "Atraso grave acumulado de 18 minutos na Linha 24. Tráfego de intensidade extrema registado na Avenida Central e Arcada devido a acidente rodoviário de terceiros.",
            "GPS Telemetria",
            "Atraso acumulado: 18 min, Velocidade média do troço: 3.8 km/h, Tempo estimado de desimpedimento: 25 min"
        );
        // Seed some history logs to show actions taken for this "EM_TRATAMENTO" alert
        delayAlerta.adicionarLogHistorico("Operador (admin@tub.pt) alterou o estado para Em Análise.");
        delayAlerta.adicionarLogHistorico("Nota: Confirmado engarrafamento massivo na Arcada devido a colisão rodoviária. Polícia de Braga já no local.");
        alertaOperacionalRepository.save(delayAlerta);
    }

    private void criarConfiguracaoIntegracao() {
        if (configuracaoIntegracaoRepository.count() == 0) {
            ConfiguracaoIntegracao config = new ConfiguracaoIntegracao();
            config.setNome("Sincronizacao Validadores");
            config.setEndpoint("http://api.tub.pt/validadores");
            config.setToken("default_token");
            config.setAtiva(true);
            config.setIntervaloMinutos(2); // default interval of 2 minutes
            config.setSimulacaoMaxEntradasSaidas(10);
            config.setSimulacaoMaxOcupacaoPercentual(90);
            configuracaoIntegracaoRepository.save(config);
        }
    }

    private void criarCatalogoMensagensRapidas() {
        if (catalogoMensagensRapidasRepository.count() == 0) {
            catalogoMensagensRapidasRepository.save(new CatalogoMensagensRapidas(
                    "Atraso Linha 43",
                    "LINHA 43 COM ATRASO DE 10 MIN DEVIDO A TRANSITO",
                    "ATRASOS"
            ));
            catalogoMensagensRapidasRepository.save(new CatalogoMensagensRapidas(
                    "Aviso Greve",
                    "SERVICOS DE TRANSPORTES PODERAO SOFRER PERTURBACOES DEVIDO A GREVE",
                    "AVISOS"
            ));
            catalogoMensagensRapidasRepository.save(new CatalogoMensagensRapidas(
                    "Boas Vindas",
                    "BEM-VINDO AOS TRANSPORTES URBANOS DE BRAGA - TUB",
                    "GERAL"
            ));
            catalogoMensagensRapidasRepository.save(new CatalogoMensagensRapidas(
                    "Desvio de Rota",
                    "LINHA 2 DESVIADA POR MOTIVO DE OBRAS NA AV. DA LIBERDADE",
                    "DESVIOS"
            ));
        }
    }
}