package com.parkinglot.vehicle;

import com.parkinglot.ParkingTicket;

public abstract class Vehicle {
    private static final String DEFAULT_LICENSE_NUMBER = "RJ01CB8292";
    private String licenseNumber;
    private String vehicleType;
    private ParkingTicket parkingTicket;

    Vehicle(String vehicleType){
        this.vehicleType = vehicleType;
    }
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
    public String getVehicleType(){
        return vehicleType;
    }
}