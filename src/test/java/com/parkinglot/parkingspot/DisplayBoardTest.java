package com.parkinglot.parkingspot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DisplayBoardTest {

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
    public void testDisplayBoardInitialization(){
        DisplayBoard displayBoard = new DisplayBoard(1);
        assertNotNull(displayBoard);
    }
    @Test
    public void testDisplayBoardUpdate(){
        DisplayBoard displayBoard = new DisplayBoard(1);
        displayBoard.update(Map.of(Large.class, 2,
                Handicapped.class, 3,
                Compact.class, 4));
        assertEquals(3, displayBoard.getVacantSpotCountMap().size());
        assertEquals(2, displayBoard.getVacantSpotCountMap().get(Large.class));
        assertEquals(3, displayBoard.getVacantSpotCountMap().get(Handicapped.class));
        assertEquals(4, displayBoard.getVacantSpotCountMap().get(Compact.class));
        displayBoard.update(Map.of(Large.class, 3,
                Motorcycle.class, 2));
        assertEquals(2, displayBoard.getVacantSpotCountMap().size());
        assertEquals(3, displayBoard.getVacantSpotCountMap().get(Large.class));
        assertEquals(2, displayBoard.getVacantSpotCountMap().get(Motorcycle.class));
    }
}
