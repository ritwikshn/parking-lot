package com.parkinglot.rest;

import jakarta.validation.constraints.*;

public class VehicleRequestDTO {

    @NotBlank(message = "vehicle type must not be blank")
    private String vehicleType;

    @NotBlank(message = "vehicle license number must not be blank")
    private String vehicleLicenseNumber;


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleLicenseNumber() {
        return vehicleLicenseNumber;
    }

    public void setVehicleLicenseNumber(String vehicleLicenseNumber) {
        this.vehicleLicenseNumber = vehicleLicenseNumber;
    }
}
