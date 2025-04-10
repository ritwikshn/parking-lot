package com.parkinglot.parkingspot;

public enum ParkingSpotStatus {
    VACANT(){
        @Override
        public <T> T accept(ParkingSpotStatusVisitor<T> visitor){
            return visitor.visitVacant();
        }
    },
    OCCUPIED(){
        @Override
        public <T> T accept(ParkingSpotStatusVisitor<T> visitor){
            return visitor.visitOccupied();
        }
    },
    RESERVED(){
        @Override
        public <T> T accept(ParkingSpotStatusVisitor<T> visitor){
            return visitor.visitReserved();
        }
    },
    UNAVAILABLE(){
        @Override
        public <T> T accept(ParkingSpotStatusVisitor<T> visitor){
            return visitor.visitUnavailable();
        }
    };

    public abstract <T> T accept(ParkingSpotStatusVisitor<T> visitor);



    public interface ParkingSpotStatusVisitor<T> {
        T visitVacant();
        T visitOccupied();
        T visitReserved();
        T visitUnavailable();
    }
}
