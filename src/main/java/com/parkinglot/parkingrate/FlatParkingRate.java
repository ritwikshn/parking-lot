package com.parkinglot.parkingrate;

public class FlatParkingRate implements ParkingRate{
    private double hourlyRate;
    private static final double DEFAULT_HOURLY_RATE = 10;
    public FlatParkingRate(){
        this(DEFAULT_HOURLY_RATE);
    }

    public FlatParkingRate(double hourlyRate){
        this.hourlyRate = hourlyRate;
    }
    public double calculate(long hours) {
        return hours * hourlyRate;
    }
}
