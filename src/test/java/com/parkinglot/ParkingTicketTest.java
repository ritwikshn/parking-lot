package com.parkinglot;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParkingTicketTest {

    @Test
    public void testParkingTicketInitialization(){
        ZonedDateTime now = ZonedDateTime.now();
        ParkingTicket ticket = new ParkingTicket(1, now);
        assertNotNull(ticket);
        assertEquals(1, ticket.getTicketNumber());
        assertEquals(now, ticket.getEntryTimestamp());
    }
}
