package com.parkinglot.service;

import com.parkinglot.Entrance;
import com.parkinglot.ParkingLot;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EntranceService {
    public Optional<Entrance> getARegisteredEntrance(){
        return ParkingLot.getInstance().getARegisteredEntrance();
    }
}
