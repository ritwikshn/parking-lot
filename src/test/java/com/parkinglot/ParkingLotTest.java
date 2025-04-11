package com.parkinglot;

import com.parkinglot.parkingrate.FlatParkingRate;
import com.parkinglot.parkingrate.ParkingRate;
import com.parkinglot.parkingspot.*;
import com.parkinglot.vehicle.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class ParkingLotTest {

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
    @BeforeEach
    void resetSingletonParkingLot(){
        try {
            Field instance = ParkingLot.class.getDeclaredField("parkingLot");
            instance.setAccessible(true);
            instance.set(null, null);
        }catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to reset ParkingLot singleton", e);
        }
    }

    @Test
    void testParkingLotDefaultInitialization(){
        ParkingLot lot = ParkingLot.getInstance();
        assertNotNull(lot, "ParkingLot should be initialized");
    }
    @Test
    void testParkingLotParameterizedInitialization1(){
        int id = 10;
        String name = "someparkingLot";
        String address = "3-daffodil-street";
        ParkingLot lot = ParkingLot.getInstance(id, name, address);
        assertNotNull(lot, "ParkingLot should be initialized");
        assertEquals(id, lot.getId());
        assertEquals(address, lot.getAddress());
    }
    @Test
    void testParkingLotParameterizedInitialization2(){
        int id = 10;
        String name = "someparkingLot";
        String address = "3-daffodil-street";
        ParkingRate rate = new FlatParkingRate(20);
        ParkingLot lot = ParkingLot.getInstance(id, name, address, rate);
        assertNotNull(lot, "ParkingLot should be initialized");
        assertEquals(id, lot.getId());
        assertEquals(address, lot.getAddress());
        assertEquals(rate, lot.getParkingRate());
    }
    @Test
    public void testGetParkingTicketWhenVacancy(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Optional<ParkingTicket> ticket = parkingLot.getParkingTicket(Car.class);
        ticket.ifPresentOrElse(
                t -> assertEquals(false, t.getAssignedSpot().isVacant()),
                () -> { throw new IllegalStateException("Parking lot is full which is not expected"); });
    }
    @Test
    public void testGetParkingTicketFailsForInvalidVehicleType(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        assertThrows(IllegalArgumentException.class, () -> parkingLot.getParkingTicket(null));
    }
    @Test
    public void testGetParkingTicketWhenNoVacancy(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Class vehicleType = Car.class;
        Class spotType = parkingLot.getParkingSpotTypeForVehicleType(vehicleType);
        int initialVacantSpotCount = parkingLot.getSpotManager().getTotalSpotCountOfType(spotType);
        while(initialVacantSpotCount -- > 0){
            parkingLot.getSpotManager().occupyAndGetEmptySpotOfType(spotType);
        }
        //now there is no more vacancy of corresponding spot type
        assertTrue(parkingLot.getParkingTicket(vehicleType).isEmpty());
    }
    @Test
    public void testParkingTicketNumbersAreUnique(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Optional<ParkingTicket> ticket1 = parkingLot.getParkingTicket(Car.class);
        Optional<ParkingTicket> ticket2 = parkingLot.getParkingTicket(Truck.class);
        Optional<ParkingTicket> ticket3 = parkingLot.getParkingTicket(Van.class);
        if(ticket1.isEmpty() || ticket2.isEmpty() || ticket3.isEmpty()){
            throw new IllegalStateException("Parking lot is full which is not expected");
        }
        assertNotEquals(ticket1.get().getTicketNumber(), ticket2.get().getTicketNumber());
        assertNotEquals(ticket2.get().getTicketNumber(), ticket3.get().getTicketNumber());
        assertNotEquals(ticket3.get().getTicketNumber(), ticket1.get().getTicketNumber());
    }
    @Test
    public void testGetSpotManager(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        ParkingSpotManager spotManager = parkingLot.getSpotManager();
        assertNotNull(spotManager);
    }
    @Test
    public void testSpotAllocationOfGivenValidSpotType(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        assertEquals(Compact.class, parkingLot.getParkingSpotTypeForVehicleType(Car.class));
        assertEquals(Large.class, parkingLot.getParkingSpotTypeForVehicleType(Truck.class));
        assertEquals(Motorcycle.class, parkingLot.getParkingSpotTypeForVehicleType(MotorCycle.class));
        assertEquals(Handicapped.class, parkingLot.getParkingSpotTypeForVehicleType(Van.class));
    }
    @Test
    public void testSpotAllocationOfGivenInvalidSpotType1(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Exception e = assertThrows(IllegalArgumentException.class, () ->  parkingLot.getParkingSpotTypeForVehicleType(VehicleFactoryTest.Jeep.class));
        assertEquals("Unknown vehicle type", e.getMessage());
    }
    @Test
    public void testSpotAllocationOfGivenInvalidSpotType2(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        Exception e = assertThrows(IllegalArgumentException.class, () ->  parkingLot.getParkingSpotTypeForVehicleType(null));
        assertEquals("Unknown vehicle type", e.getMessage());
    }
    @Test
    public void testSpawnAndFetchNewExit(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        parkingLot.unregisterAllExitsAndEntrances();
        Exit exit = parkingLot.spawnNewExit();
        assertNotNull(exit);
        //since this is the only registered exit at this moment
        assertEquals(exit, parkingLot.getARegisteredExit());
    }
    @Test
    public void testSpawnAndFetchNewEntrance(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        parkingLot.unregisterAllExitsAndEntrances();
        Entrance entry= parkingLot.spawnNewEntrance();
        assertNotNull(entry);
        //since this is the only registered entrance at this moment
        assertEquals(entry, parkingLot.getARegisteredEntrance().get());
    }

    @Test
    public void testSpawnAndFetchNewDisplayBoard(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        ParkingSpotManager spotManager = parkingLot.getSpotManager();
        DisplayBoard board = parkingLot.spawnNewDisplayBoard();
        for(Map.Entry<Class<? extends ParkingSpot>, Integer> entry: board.getVacantSpotCountMap().entrySet()){
            assertEquals(spotManager.getVacantSpotCountOfType(entry.getKey()), entry.getValue());
        }
    }
    @Test
    public void testSpotRelease(){
        ParkingLot parkingLot = ParkingLot.getInstance();
        ParkingSpot spot = parkingLot.getSpotManager().occupyAndGetEmptySpotOfType(Large.class);
        assertEquals(false, spot.isVacant());

        parkingLot.releaseParkingSpot(spot);
        assertEquals(true, spot.isVacant());
    }

}
