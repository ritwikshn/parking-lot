package com.parkinglot.vehicle;

import com.parkinglot.ParkingTicket;

public class Truck extends Vehicle {
    public final static String VEHICLE_TYPE = "truck";

    Truck() {
        super(VEHICLE_TYPE);
    }

    @Override
    public void park() {
        super.park();
        System.out.println("Truck is now parking.....parked");
    }

    @Override
    public void exit() {
        System.out.println("Truck is now exiting.....");
    }
}
