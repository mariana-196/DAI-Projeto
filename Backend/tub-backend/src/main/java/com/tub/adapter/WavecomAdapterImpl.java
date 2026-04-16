package com.tub.adapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tub.model.PassengerCount;

@Component
public class WavecomAdapterImpl implements WavecomAdapter {

    @Override
    public List<PassengerCount> getPassengerCounts() {
        // Como não estamos ligados fisicamente aos autocarros, 
        // criamos uma simulação (Mock) dos dados que o sensor enviaria.
        List<PassengerCount> listaContagens = new ArrayList<>();
        
        PassengerCount sensorPortaFrente = new PassengerCount();
        sensorPortaFrente.setVehicleId("AUT-001");
        sensorPortaFrente.setStopId("PARAGEM-AV-LIBERDADE");
        sensorPortaFrente.setPassengersIn(4);
        sensorPortaFrente.setPassengersOut(1);
        sensorPortaFrente.setTimestamp(LocalDateTime.now());
        
        PassengerCount sensorPortaTras = new PassengerCount();
        sensorPortaTras.setVehicleId("AUT-001");
        sensorPortaTras.setStopId("PARAGEM-AV-LIBERDADE");
        sensorPortaTras.setPassengersIn(0);
        sensorPortaTras.setPassengersOut(5);
        sensorPortaTras.setTimestamp(LocalDateTime.now());

        listaContagens.add(sensorPortaFrente);
        listaContagens.add(sensorPortaTras);
        
        return listaContagens;
    }
}