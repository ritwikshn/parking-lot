package com.parkinglot.parkingspot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SpotManagerTest {

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
    public void testParkingSpotManagerInitialization(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance();
        assertNotNull(spotManager, "Parking spot manager is null");
    }
    @Test
    public void testParkingSpotCount(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 4,
                        Large.class, 4,
                        Handicapped.class, 4,
                        Motorcycle.class, 4));
        assertEquals(4, spotManager.getTotalSpotCountOfType(Compact.class));
        assertEquals(4, spotManager.getTotalSpotCountOfType(Motorcycle.class));
        assertEquals(4, spotManager.getTotalSpotCountOfType(Large.class));
        assertEquals(4, spotManager.getTotalSpotCountOfType(Handicapped.class));
    }
    @Test
    public void testDefaultParkingSpotCount(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance();
        assertEquals(3, spotManager.DEFAULT_INITIAL_PARKING_SPOT_COUNT.get(Compact.class));
        assertEquals(3, spotManager.DEFAULT_INITIAL_PARKING_SPOT_COUNT.get(Motorcycle.class));
        assertEquals(3, spotManager.DEFAULT_INITIAL_PARKING_SPOT_COUNT.get(Large.class));
        assertEquals(3, spotManager.DEFAULT_INITIAL_PARKING_SPOT_COUNT.get(Handicapped.class));
    }

    @Test
    public void testSpotAllocation(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 4,
                        Large.class, 4,
                        Handicapped.class, 4,
                        Motorcycle.class, 4));
        ParkingSpot allocatedCompactSpot1 = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertEquals(3, spotManager.getVacantSpotCountOfType(Compact.class));
        assertEquals(false, allocatedCompactSpot1.isVacant());
        ParkingSpot allocatedCompactSpot2 = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertEquals(2, spotManager.getVacantSpotCountOfType(Compact.class));
        assertEquals(false, allocatedCompactSpot2.isVacant());
        ParkingSpot allocatedCompactSpot3 = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertEquals(1, spotManager.getVacantSpotCountOfType(Compact.class));
        assertEquals(false, allocatedCompactSpot3.isVacant());
        ParkingSpot allocatedCompactSpot4 = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertEquals(0, spotManager.getVacantSpotCountOfType(Compact.class));
        assertEquals(false, allocatedCompactSpot4.isVacant());
        ParkingSpot nullSpot = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertEquals(0, spotManager.getVacantSpotCountOfType(Compact.class));
        assertNull(nullSpot);
        ParkingSpot allocatedLargeSpot = spotManager.occupyAndGetEmptySpotOfType(Large.class);
        assertEquals(3, spotManager.getVacantSpotCountOfType(Large.class));
        assertEquals(false, allocatedLargeSpot.isVacant());
    }
    @Test
    public void testSpotCountResizing(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 4,
                        Large.class, 4,
                        Handicapped.class, 4,
                        Motorcycle.class, 4));
        spotManager.resizeSpotCountToAMin(Large.class, 6);
        assertEquals(6, spotManager.getTotalSpotCountOfType(Large.class));
        //does not resize down which is expected
        spotManager.resizeSpotCountToAMin(Large.class, 2);
        assertEquals(6, spotManager.getTotalSpotCountOfType(Large.class));
    }
    @Test
    public void testDisplayBoardNotification(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 4,
                        Large.class, 4,
                        Handicapped.class, 4,
                        Motorcycle.class, 4));
        DisplayBoard board = new DisplayBoard(1);
        spotManager.addSpotObserver(board);
        assertEquals(4, board.getVacantSpotCountMap().size());
        assertEquals(4, board.getVacantSpotCountMap().get(Compact.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Large.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Handicapped.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Motorcycle.class));

        ParkingSpot largeSpot = spotManager.occupyAndGetEmptySpotOfType(Large.class);
        //wait for notification process to complete
        CompletableFuture<Void> future = spotManager.notifyObservers();
        future.join();

        assertEquals(3, board.getVacantSpotCountMap().get(Large.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Compact.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Handicapped.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Motorcycle.class));

        for(int i = 0; i< 2; i++){
            spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        }
        //wait for notification process to complete
        future = spotManager.notifyObservers();
        future.join();

        assertEquals(2, board.getVacantSpotCountMap().get(Compact.class));
        assertEquals(3, board.getVacantSpotCountMap().get(Large.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Handicapped.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Motorcycle.class));

        spotManager.releaseSpot(largeSpot);
        //wait for notification process to complete
        future = spotManager.notifyObservers();
        future.join();

        assertEquals(4, board.getVacantSpotCountMap().get(Large.class));
        assertEquals(2, board.getVacantSpotCountMap().get(Compact.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Handicapped.class));
        assertEquals(4, board.getVacantSpotCountMap().get(Motorcycle.class));
    }
    @Test
    public void testValidReleaseSpot(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 1));
        ParkingSpot allocatedCompactSpot1 = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertNotNull(allocatedCompactSpot1);

        spotManager.releaseSpot(allocatedCompactSpot1);

        //assert that the spot is vacant after release
        assertEquals(true, allocatedCompactSpot1.isVacant());

        //assert that this spot is assignable once more
        ParkingSpot allocatedCompactSpot2 = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        assertSame(allocatedCompactSpot1, allocatedCompactSpot2);
    }
    @Test
    public void testInvalidReleaseSpot1(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance();
        //a spot not created via spot manager
        ParkingSpot unregisteredSpot = new Large(1, "L" + 1);
        assertNotNull(unregisteredSpot);
        assertThrows(IllegalStateException.class, () -> spotManager.releaseSpot(unregisteredSpot));
    }
    @Test
    public void testInvalidReleaseSpot2(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 1));
        //a spot not created via spot manager
        ParkingSpot nullSpot = null;

        assertThrows(IllegalArgumentException.class, () -> spotManager.releaseSpot(nullSpot));
    }
    @Test
    public void testReleaseSpotThatIsAlreadyVacant(){
        ParkingSpotManager spotManager = ParkingSpotManager.getInstance(
                Map.of(Compact.class, 1));
        ParkingSpot spot = spotManager.occupyAndGetEmptySpotOfType(Compact.class);
        //this should be fine
        spotManager.releaseSpot(spot);
        assertEquals(1, spotManager.getVacantSpotCountOfType(spot.getClass()));

        //now the spot is vacant
        //try to release again
        boolean success = spotManager.releaseSpot(spot);
        assertEquals(true, success);
        //the queue should not contain duplicate entry
        assertEquals(1, spotManager.getVacantSpotCountOfType(spot.getClass()));
    }
}
