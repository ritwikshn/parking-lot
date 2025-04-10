package com.parkinglot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntranceTest {

    @Test
    public void testEntranceInitialization(){
        Entrance entryGate = new Entrance(1);
        assertNotNull(entryGate, "Entry gate is null");
    }
}
