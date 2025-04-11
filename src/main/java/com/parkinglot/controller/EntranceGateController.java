package com.parkinglot.controller;

import com.parkinglot.Entrance;
import com.parkinglot.ParkingTicket;
import com.parkinglot.rest.ParkingTicketResponse;
import com.parkinglot.service.EntranceService;
import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.vehicle.VehicleFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/parkingLot")
public class EntranceGateController {

    EntranceService entranceService;

    public EntranceGateController(EntranceService entranceService){
        this.entranceService = entranceService;
    }

    @GetMapping("/entranceGate/assignAndGetParkingTicket")
    public ParkingTicketResponse assignAndGetParkingTicket(@RequestParam String vehicleType,
                                                           @RequestParam String vehicleLicenseNumber){
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, vehicleLicenseNumber);
        Optional<Entrance> entrance = entranceService.getARegisteredEntrance();

        Optional<ParkingTicket> ticket = entrance.orElseThrow(() -> new RuntimeException("Failure. No entrance to the parking lot"))
                                                 .assignAndGetParkingTicket(vehicle);
        if(ticket.isEmpty())
            throw new RuntimeException("Failure. This is possibly due to unavailability of parking space.");

        return new ParkingTicketResponse(ticket.get());

    }
}
