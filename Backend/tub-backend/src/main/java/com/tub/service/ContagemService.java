package com.tub.service;

import com.tub.model.PassengerCount;
// IMPORTANTE: Confirma que os imports abaixo correspondem aos pacotes corretos no teu projeto
// import com.tub.model.LotacaoViatura;
// import com.tub.model.HistoricoLotacao;
// import com.tub.repository.LotacaoViaturaRepository;
// import com.tub.repository.HistoricoLotacaoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContagemService {

    /* DESCOMENTAR QUANDO TIVERES OS REPOSITÓRIOS IMPORTADOS
    @Autowired
    private LotacaoViaturaRepository lotacaoRepository;

    @Autowired
    private HistoricoLotacaoRepository historicoRepository;
    */

    /**
     * Linha 55: Controlo Contagem
     * Processa a lista de movimentos, atualiza a lotação e guarda no histórico.
     */
    public void processarContagens(List<PassengerCount> contagens) {
        
        for (PassengerCount contagem : contagens) {
            String idViatura = contagem.getVehicleId();
            
            /* LÓGICA DE BASE DE DADOS (Exemplo de como será com o teu Repository):
            
            // 1. Vai buscar o autocarro à base de dados
            LotacaoViatura viatura = lotacaoRepository.findById(idViatura)
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada: " + idViatura));
            
            // 2. Faz a matemática: Atual + Entradas - Saídas
            int novaLotacao = viatura.getPassageirosAtuais() + contagem.getPassengersIn() - contagem.getPassengersOut();
            
            // Garante que não há valores negativos por falhas de sensor
            if (novaLotacao < 0) novaLotacao = 0;
            
            // 3. Atualiza os dados do autocarro
            viatura.setPassageirosAtuais(novaLotacao);
            
            // Calcula a taxa de ocupação (percentagem)
            double taxa = (double) novaLotacao / viatura.getCapacidadeMaxima() * 100;
            viatura.setTaxaOcupacao(taxa);
            
            // 4. Grava o estado atualizado na Base de Dados (Linha 56)
            lotacaoRepository.save(viatura);
            
            // 5. Cria um registo para o Histórico (Linha 57)
            HistoricoLotacao historico = new HistoricoLotacao();
            historico.setVehicleId(idViatura);
            historico.setStopId(contagem.getStopId());
            historico.setPassageirosIn(contagem.getPassengersIn());
            historico.setPassageirosOut(contagem.getPassengersOut());
            historico.setTimestamp(contagem.getTimestamp());
            historicoRepository.save(historico);
            
            */
            
            // PRINT PARA TESTE ENQUANTO OS REPOSITÓRIOS ESTÃO COMENTADOS:
            System.out.println("Processado -> Viatura: " + idViatura + 
                               " | Entraram: " + contagem.getPassengersIn() + 
                               " | Sairam: " + contagem.getPassengersOut());
        }
    }
}