package com.parkinglot.vehicle;

import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.ParkingTicket;

public class Van extends Vehicle {
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
