package com.tub.p8_gestao_bilhetica.service;

import com.tub.p5_lotacao.repository.LinhaRepository;
import com.tub.p8_gestao_bilhetica.repository.LoteDadosBilheticaRepository;
import com.tub.p8_gestao_bilhetica.repository.RegistoBilheticaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RegistoBilheticaRepository registoRepository;
    private final LoteDadosBilheticaRepository loteRepository;
    private final LinhaRepository linhaRepository;

    public DataLoader(RegistoBilheticaRepository registoRepository, LoteDadosBilheticaRepository loteRepository, LinhaRepository linhaRepository) {
        this.registoRepository = registoRepository;
        this.loteRepository = loteRepository;
        this.linhaRepository = linhaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicialização de dados removida. O utilizador faz o upload do CSV via interface gráfica.
    }
}
