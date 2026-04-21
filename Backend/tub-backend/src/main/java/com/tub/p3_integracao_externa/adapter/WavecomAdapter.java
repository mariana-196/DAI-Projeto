package com.tub.p3_integracao_externa.adapter;

import java.util.List;

import com.tub.p3_integracao_externa.model.PassengerCount;

public interface WavecomAdapter {

    List<PassengerCount> getPassengerCounts();

}