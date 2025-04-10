package com.parkinglot.integration;

import com.parkinglot.Entrance;
import com.parkinglot.Exit;
import com.parkinglot.ParkingLot;
import com.parkinglot.ParkingTicket;
import com.parkinglot.exceptions.PermanentPaymentException;
import com.parkinglot.parkingspot.ParkingSpotManager;
import com.parkinglot.payment.CardPayment;
import com.parkinglot.payment.Payment;
import com.parkinglot.vehicle.VehicleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.vehicle.Car;
import com.parkinglot.parkingspot.ParkingSpot;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class ParkingLotIntegrationTest {
    @BeforeEach
    void resetParkingSpotManagerSingleton(){
        try {
            Field instance = ParkingSpotManager.class.getDeclaredField("spotManager");
            instance.setAccessible(true);
            instance.set(null, null);
        }catch(NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException("Failed to reset parking spot manager singleton");
        }
    }
    @Test
    public void testEntireVehicleParkingFlow(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Vehicle car = VehicleFactory.createVehicle(Car.class, "CAR123");
        Entrance entryGate = parkingLot.spawnNewEntrance();
        Exit exitGate = parkingLot.spawnNewExit();

        Optional<ParkingTicket> optTicket = entryGate.getParkingTicket(car);
        if(optTicket.isEmpty()) throw new IllegalStateException("Parking Lot is full which is not expected");
        ParkingTicket ticket = optTicket.get();

        car.assignTicket(ticket);
        car.park();

        double parkingFee = exitGate.presentTicketAndGetFee(ticket);
        Payment cardPayment = new CardPayment(parkingFee);
        try {
            //Payment may fail
            //Retry few times, if still fails give up
            int retries = 3;
            boolean success = false;
            while(!success && retries-- > 0){
                success = exitGate.processParkingTicketPayment(ticket, cardPayment);
            }
        }catch(PermanentPaymentException e){
            throw new RuntimeException("Payment failure", e);
        }
        car.exit();
    }
    @Test
    public void testAssignedSpotReleasedAtExit(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Vehicle car = VehicleFactory.createVehicle(Car.class, "CAR123");
        Entrance entryGate = parkingLot.spawnNewEntrance();
        Exit exitGate = parkingLot.spawnNewExit();

        Optional<ParkingTicket> optTicket = entryGate.getParkingTicket(car);
        if(optTicket.isEmpty()) throw new IllegalStateException("Parking Lot is full which is not expected");
        ParkingTicket ticket = optTicket.get();

        car.assignTicket(ticket);
        car.park();

        ParkingSpot assignedSpot = ticket.getAssignedSpot();
        assertNotNull(assignedSpot);
        assertFalse(assignedSpot.isVacant());

        //calling this also releases the assigned spot
        exitGate.presentTicketAndGetFee(ticket);

        //stall this thread for a bit and hope that the thread
        //to release spot in background is scheduled meanwhile
        try{
            Thread.sleep(100);
        }catch(InterruptedException e){}
        assertTrue(assignedSpot.isVacant());
    }
}
