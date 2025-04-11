package com.parkinglot.vehicle;

import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.ParkingTicket;
public class Car extends Vehicle {

    public final static String VEHICLE_TYPE = "car";

    public Car(){
        super(VEHICLE_TYPE);
    }

    @Override
    public void park() {
        super.park();
        System.out.println("Car is now parking.....parked");
    }

    @Override
    public void exit() {
        System.out.println("Car is now exiting.....");
    }
}
