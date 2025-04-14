package com.parkinglot;

import com.parkinglot.exception.AmountMismatchException;
import com.parkinglot.exception.PermanentPaymentException;
import com.parkinglot.parkingrate.FlatParkingRate;
import com.parkinglot.parkingrate.ParkingRate;
import com.parkinglot.payment.CardPayment;
import com.parkinglot.payment.CashPayment;
import com.parkinglot.payment.Payment;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ExitTest {

    @Test
    public void testExitGateInitialization(){
        Exit exitGate = new Exit(1);
        assertNotNull(exitGate);
    }
    @Test
    public void testParkingDurationCalculation() {
        Exit exitGate = new Exit(1);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime twoHours = now.plusHours(1).plusMinutes(10);
        ZonedDateTime threeHours = now.plusHours(3);
        //1hour10mins => 2 hours in count
        assertEquals(2, exitGate.calcDurationInHours(now, twoHours));
        assertEquals(3, exitGate.calcDurationInHours(now, threeHours));
    }
    @Test
    public void testValidParkingFeeCalculation(){
        Exit exitGate = new Exit(1);
        ZonedDateTime entryTimestamp = ZonedDateTime.now();
        ZonedDateTime exitTimestamp = entryTimestamp.plusHours(2).plusMinutes(10);
        ParkingTicket ticket = new ParkingTicket(10, entryTimestamp);
        ticket.setExitTimeStamp(exitTimestamp);
        ParkingRate flatRate = new FlatParkingRate(10);

        double fee = exitGate.calculateParkingFee(ticket, flatRate);
        //2hours10mins => 3 hours in count
        //expected_fee = 3*10 = 30
        assertEquals(30, fee);
    }
    @Test
    public void testInvalidParkingFeeCalculation1(){
        Exit exitGate = new Exit(1);
        ZonedDateTime entryTimestamp = ZonedDateTime.now();
        ZonedDateTime exitTimestamp = entryTimestamp.plusHours(2).plusMinutes(10);
        ParkingTicket ticket = new ParkingTicket(10, entryTimestamp);
        ticket.setExitTimeStamp(exitTimestamp);
        ParkingRate flatRate = new FlatParkingRate(-10);
        Exception e = assertThrows(IllegalStateException.class, () -> exitGate.calculateParkingFee(ticket, flatRate));
        assertEquals("Parking fee cannot be negative", e.getMessage());
    }
    @Test
    public void testInvalidParkingFeeCalculation2(){
        Exit exitGate = new Exit(1);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = now.plusHours(2).plusMinutes(10);
        //exit before entry, which is invalid
        ParkingTicket ticket = new ParkingTicket(10, later);
        ticket.setExitTimeStamp(now);
        ParkingRate flatRate = new FlatParkingRate(-10);
        Exception e = assertThrows(IllegalStateException.class, () -> exitGate.calculateParkingFee(ticket, flatRate));
        assertEquals("Parking duration cannot be negative", e.getMessage());
    }
    @Test
    public void testInvalidParkingFeeCalculation3(){
        Exit exitGate = new Exit(1);
        Exception e = assertThrows(IllegalArgumentException.class, () -> exitGate.calculateParkingFee(null, new FlatParkingRate(10)));
        assertEquals("Parking ticket cannot be null", e.getMessage());
    }

    @Test
    public void testInvalidParkingFeeCalculation4(){
        Exit exitGate = new Exit(1);
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> exitGate.calculateParkingFee(new ParkingTicket(1, ZonedDateTime.now()), null));
        assertEquals("Parking rate cannot be null", e.getMessage());
    }

    @Test
    public void testInvalidProcessParkingPayment1(){
        Exit exitGate = new Exit(1);
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> exitGate.processParkingTicketPayment(new ParkingTicket(1, ZonedDateTime.now()), null));
        assertEquals("Payment option cannot be null", e.getMessage());
    }

    @Test
    public void testInvalidProcessParkingPayment2(){
        Exit exitGate = new Exit(1);
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> exitGate.processParkingTicketPayment(null, new CardPayment(10)));
        assertEquals("Parking ticket cannot be null", e.getMessage());
    }

    @Test
    public void testValidProcessParkingPaymentViaCard(){
        Exit exitGate = new Exit(1);

        ZonedDateTime entryTimestamp = ZonedDateTime.now();
        ZonedDateTime exitTimestamp = entryTimestamp.plusHours(2).plusMinutes(10);
        ParkingTicket ticket = new ParkingTicket(10, entryTimestamp);
        ticket.setExitTimeStamp(exitTimestamp);

        ParkingRate flatRate = new FlatParkingRate(20);

        double amount = exitGate.calculateParkingFee(ticket, flatRate);
        ticket.setAmount(amount);

        Payment cardPayment  = new CardPayment(amount);
        try {
            boolean paymentSuccess = exitGate.processParkingTicketPayment(ticket, cardPayment, flatRate);
            //payment may fail but that should only be temporary,
            //so it is better to assert that the payment is successful
            assertTrue(paymentSuccess);
        }catch(PermanentPaymentException e){
            throw new RuntimeException("Permanent payment failure", e);
        }
    }
    @Test
    public void testValidProcessParkingPaymentViaCash(){
        Exit exitGate = new Exit(1);

        ZonedDateTime entryTimestamp = ZonedDateTime.now();
        ZonedDateTime exitTimestamp = entryTimestamp.plusHours(2).plusMinutes(10);
        ParkingTicket ticket = new ParkingTicket(10, entryTimestamp);
        ticket.setExitTimeStamp(exitTimestamp);

        ParkingRate flatRate = new FlatParkingRate(20);

        double amount = exitGate.calculateParkingFee(ticket, flatRate);
        Payment cashPayment  = new CashPayment(amount);

        try {
            boolean paymentSuccess = exitGate.processParkingTicketPayment(ticket, cashPayment, flatRate);
            //payment may fail but that should only be temporary,
            //so it is better to assert that the payment is successful
            assertTrue(paymentSuccess);
        }catch(PermanentPaymentException e){
            throw new RuntimeException("Permanent payment failure", e);
        }
    }

    @Test
    public void testInvalidProcessParkingPayment3(){
        Exit exitGate = new Exit(1);
        ZonedDateTime entryTimestamp = ZonedDateTime.now();
        ZonedDateTime exitTimestamp = entryTimestamp.plusHours(2).plusMinutes(10);
        ParkingTicket ticket = new ParkingTicket(10, entryTimestamp);
        ticket.setExitTimeStamp(exitTimestamp);

        ParkingRate flatRate = new FlatParkingRate(20);

        double amount = exitGate.calculateParkingFee(ticket, flatRate);
        //amount mismatch
        Payment cardPayment  = new CardPayment(amount + 10);

        Exception e = assertThrows(AmountMismatchException.class, () -> exitGate.processParkingTicketPayment(ticket, cardPayment, flatRate));
        assertEquals("Amount in the payment should match amount on the ticket", e.getMessage());
    }

}
