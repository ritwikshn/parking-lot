package com.parkinglot;

import com.parkinglot.account.Account;
import com.parkinglot.exceptions.AmountMismatchException;
import com.parkinglot.exceptions.PermanentPaymentException;
import com.parkinglot.parkingrate.ParkingRate;
import com.parkinglot.parkingspot.ParkingSpot;
import com.parkinglot.payment.Payment;
import com.parkinglot.privilege.ForceOpenExitGateSystemPrivilege;
import com.parkinglot.privilege.ForceOpenExitGateSystemPrivilege.IForceOpenExitGatePrivilegeFulfiller;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class Exit implements IForceOpenExitGatePrivilegeFulfiller {
    private int id;
    Exit(int id){
        this.id = id;
    }

    long calcDurationInHours(ZonedDateTime time1, ZonedDateTime time2){
        return (Duration.between(time1, time2).toMinutes() + 59)/60;
    }
    protected double calculateParkingFee(ParkingTicket ticket){
        return calculateParkingFee(ticket, ParkingLot.getInstance().getParkingRate());
    }
    protected double calculateParkingFee(ParkingTicket ticket, ParkingRate parkingRate){
        if(ticket == null) {
            throw new IllegalArgumentException("Parking ticket cannot be null");
        }

        if(parkingRate == null){
            throw new IllegalArgumentException("Parking rate cannot be null");
        }

        ZonedDateTime exitTime = (ticket.getExitTimeStamp() != null ? ticket.getExitTimeStamp() : ZonedDateTime.now());

        long parkingDuration = calcDurationInHours(ticket.getEntryTimestamp(), exitTime);
        if(parkingDuration < 0){
            throw new IllegalStateException("Parking duration cannot be negative");
        }

        double parkingFee = parkingRate.calculate(parkingDuration);
        if(parkingFee < 0){
            throw new IllegalStateException("Parking fee cannot be negative");
        }

        return parkingFee;
    }
    public double presentTicketAndGetFee(ParkingTicket ticket){
        if(ticket == null) {
            throw new IllegalArgumentException("Parking ticket cannot be null");
        }
        ticket.setExitTimeStamp(ZonedDateTime.now());

        //release the parking spot that was assigned to this vehicle
        //executes in background
        releaseParkingSpot(ticket.getAssignedSpot());

        return calculateParkingFee(ticket);
    }
    public boolean processParkingTicketPayment(ParkingTicket ticket, Payment payment) throws PermanentPaymentException {
        return processParkingTicketPayment(ticket, payment, ParkingLot.getInstance().getParkingRate());
    }

    private CompletableFuture<Void> releaseParkingSpot(ParkingSpot spot){
        //release the spot in the background so that caller does not stall
        return CompletableFuture.runAsync(() -> {
            ParkingLot.getInstance().releaseParkingSpot(spot);
        });
    }

    public boolean processParkingTicketPayment(ParkingTicket ticket, Payment payment, ParkingRate parkingRate) throws PermanentPaymentException {
        if(ticket == null){
            throw new IllegalArgumentException("Parking ticket cannot be null");
        }
        if(payment == null){
            throw new IllegalArgumentException("Payment option cannot be null");
        }

        if(ticket.getExitTimeStamp() == null) {
            ticket.setExitTimeStamp(ZonedDateTime.now());
        }

        ticket.setPayment(payment);
        ticket.setAmount(calculateParkingFee(ticket, parkingRate));

        //amount validation
        if(ticket.getPayment().getAmount() != ticket.getAmount()){
            throw new AmountMismatchException("Amount in the payment should match amount on the ticket");
        }

        boolean success =  ticket.getPayment().processPayment();
        if(success) openExitGate();
        return success;
    }
    private void openExitGate(){
        System.out.println("Exit gate now opened......1...2...3....closed.");
    }

    @Override
    public boolean forceOpenExitGate(Account requester) {
        if(requesterHasPrivilege(new ForceOpenExitGateSystemPrivilege(), requester)){
            openExitGate();
            return true;
        }
        return false;
    }
}
