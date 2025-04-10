package com.parkinglot.parkingspot;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DisplayBoard implements ParkingSpotObserver {
    private int id;
    private Map<Class<? extends ParkingSpot>, Integer> vacantSpotCount = new HashMap();
    public DisplayBoard(int id){
        this.id = id;
    }
    public void showFreeSlot(){
        for(Map.Entry<Class<? extends ParkingSpot>, Integer> entry: vacantSpotCount.entrySet()){
            System.out.println("Vacant spot count for spot type " + entry.getKey()  + " is " + entry.getValue());
        }
    }
    @Override
    public void update(Map<Class<? extends ParkingSpot>, Integer> spotCount) {
        //assign vacantSpotCount an identical map to spotCount
        vacantSpotCount = new HashMap<>(spotCount);
    }

    //for testing purpose
    public Map<Class<? extends ParkingSpot>, Integer> getVacantSpotCountMap(){
        return new HashMap<>(vacantSpotCount);
    }
}
