package com.parkinglot.vehicle;

import com.parkinglot.ParkingTicket;

public class MotorCycle extends Vehicle {
    public final static String VEHICLE_TYPE = "motorcycle";

    MotorCycle() {
        super(VEHICLE_TYPE);
    }

    @Override
    public void park() {
        super.park();
        System.out.println("Motorcycle is now parking......parked");
    }
    @Override
    public void exit() {
        System.out.println("Motorcycle is now exiting......");
    }

}
