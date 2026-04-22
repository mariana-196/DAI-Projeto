package com.tub.p9_monitorizacao_iot.service;

import org.springframework.stereotype.Service;
import com.tub.p3_integracao_externa.model.PassengerCount;

import java.util.List;

@Service
public class ControladorMonitorizacaoLotacao {

    /*
    @Autowired
    private LotacaoViaturaRepository lotacaoRepository;

    @Autowired
    private HistoricoLotacaoRepository historicoRepository;
    */

    public void processarContagens(List<PassengerCount> contagens) {

        for (PassengerCount contagem : contagens) {
            String idViatura = contagem.getVehicleId();

            /*
            LotacaoViatura viatura = lotacaoRepository.findById(idViatura)
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada: " + idViatura));

            int novaLotacao = viatura.getPassageirosAtuais() + contagem.getPassengersIn() - contagem.getPassengersOut();

            if (novaLotacao < 0) novaLotacao = 0;

            viatura.setPassageirosAtuais(novaLotacao);

            double taxa = (double) novaLotacao / viatura.getCapacidadeMaxima() * 100;
            viatura.setTaxaOcupacao(taxa);

            lotacaoRepository.save(viatura);

            HistoricoLotacao historico = new HistoricoLotacao();
            historico.setVehicleId(idViatura);
            historico.setStopId(contagem.getStopId());
            historico.setPassageirosIn(contagem.getPassengersIn());
            historico.setPassageirosOut(contagem.getPassengersOut());
            historico.setTimestamp(contagem.getTimestamp());
            historicoRepository.save(historico);
            */

            System.out.println("Processado -> Viatura: " + idViatura +
                               " | Entraram: " + contagem.getPassengersIn() +
                               " | Sairam: " + contagem.getPassengersOut());
        }
    }
}