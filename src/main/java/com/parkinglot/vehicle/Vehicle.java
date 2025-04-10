package com.parkinglot.vehicle;

import com.parkinglot.ParkingTicket;

public abstract class Vehicle {
    private String licenseNumber;
    private ParkingTicket parkingTicket;
    private String vehicleType;

    Vehicle(){}
    public final void assignTicket(ParkingTicket parkingTicket){
        this.parkingTicket = parkingTicket;
    }
    public void park(){
        if(this.parkingTicket == null){
            throw new IllegalStateException("Cannot park until a ticket is assigned");
        }
    }
    public abstract void exit();
    void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    public ParkingTicket showParkingTicket(){
        return this.parkingTicket;
    }
}