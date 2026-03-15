package com.tub.model;
import java.time.LocalDateTime;

public class PassengerCount {
    
    private String vehicleId;
    private String stopId;
    private int passengersIn;
    private int passengersOut;
    private LocalDateTime timestamp;

    public PassengerCount() {}

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public int getPassengersIn() {
        return passengersIn;
    }

    public void setPassengersIn(int passengersIn) {
        this.passengersIn = passengersIn;
    }

    public int getPassengersOut() {
        return passengersOut;
    }

    public void setPassengersOut(int passengersOut) {
        this.passengersOut = passengersOut;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}