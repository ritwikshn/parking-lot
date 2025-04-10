package com.parkinglot.parkingspot;

import java.util.Map;

public interface ParkingSpotObserver {
    void update(Map<Class<? extends ParkingSpot>, Integer> spotCount);
}
