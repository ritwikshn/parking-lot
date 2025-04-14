package com.parkinglot.controller;

import com.parkinglot.Entrance;
import com.parkinglot.ParkingTicket;
import com.parkinglot.rest.ParkingTicketResponseDTO;
import com.parkinglot.rest.VehicleRequestDTO;
import com.parkinglot.service.EntranceService;
import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.vehicle.VehicleFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/parkingLot")
public class EntranceGateController {

    EntranceService entranceService;

    public EntranceGateController(EntranceService entranceService){
        this.entranceService = entranceService;
    }

    @PostMapping("/entranceGate/assignAndGetParkingTicket")
    public ParkingTicketResponseDTO assignAndGetParkingTicket(@Valid @RequestBody VehicleRequestDTO request){
        Vehicle vehicle = VehicleFactory.createVehicle(request.getVehicleType(), request.getVehicleLicenseNumber());
        Optional<Entrance> entrance = entranceService.getARegisteredEntrance();

        Optional<ParkingTicket> ticket = entrance.orElseThrow(() -> new RuntimeException("Failure. No entrance to the parking lot"))
                                                 .assignAndGetParkingTicket(vehicle);
        if(ticket.isEmpty())
            throw new RuntimeException("Failure. This is possibly due to unavailability of parking space.");

        return new ParkingTicketResponseDTO(ticket.get());

    }
}
