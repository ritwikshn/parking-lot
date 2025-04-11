package com.parkinglot;

import com.parkinglot.vehicle.Vehicle;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

public class Entrance {
    private int id;

    Entrance(int id){
        this.id = id;
    }
    public Optional<ParkingTicket> getParkingTicket(Vehicle vehicle) {
        ZonedDateTime entryTime = ZonedDateTime.now();
        return getParkingTicket(vehicle, entryTime);
    }
    public Optional<ParkingTicket> getParkingTicket(Vehicle vehicle, ZonedDateTime entryTime) {
        return ParkingLot.getInstance().getParkingTicket(vehicle.getClass(), entryTime);
    }

    public Optional<ParkingTicket> assignAndGetParkingTicket(Vehicle vehicle){
        Optional<ParkingTicket> ticket = getParkingTicket(vehicle);
        ticket.ifPresent((t -> vehicle.assignTicket(t)));
        return ticket;
    }

}
