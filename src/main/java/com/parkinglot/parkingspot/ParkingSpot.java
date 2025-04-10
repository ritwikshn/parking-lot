package com.parkinglot.parkingspot;

public abstract class ParkingSpot {
    private int id;
    private ParkingSpotStatus status = ParkingSpotStatus.VACANT;
    public boolean isVacant(){
        return ParkingSpotStatus.VACANT == status;
    }
    ParkingSpot(){ }
    ParkingSpot(int id){
        this.id = id;
    }
    final synchronized void occupySpot(){
        if(!isVacant()){
            throw new IllegalStateException("Spot is not empty");
        }
        status = ParkingSpotStatus.OCCUPIED;
    }
    void vacateSpot(){
        this.status.accept(new ParkingSpotStatus.ParkingSpotStatusVisitor<Void>() {
            @Override
            public Void visitVacant() {
                status = ParkingSpotStatus.VACANT;
                return null;
            }
            @Override
            public Void visitOccupied() {
                return visitVacant();
            }
            @Override
            public Void visitReserved() {
                return visitVacant();
            }
            @Override
            public Void visitUnavailable() {
                throw new IllegalStateException("Spot is unavailable");
            }
        });
    }
}

