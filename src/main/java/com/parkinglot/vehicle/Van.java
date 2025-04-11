package com.parkinglot.vehicle;

import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.ParkingTicket;

public class Van extends Vehicle {
    public final static String VEHICLE_TYPE = "van";

    Van(){
        super(VEHICLE_TYPE);
    }

    @Override
    public void park() {
        super.park();
        System.out.println("Van is now parking.....parked");
    }
    @Override
    public void exit() {
        System.out.println("Van is now exiting.....");
    }
}
