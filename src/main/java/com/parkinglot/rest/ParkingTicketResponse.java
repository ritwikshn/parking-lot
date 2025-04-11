package com.parkinglot.rest;

import java.time.ZonedDateTime;
import com.parkinglot.ParkingTicket;

public class ParkingTicketResponse {

    private int ticketNumber;
    private ZonedDateTime entryTimestamp;
    private ZonedDateTime exitTimeStamp;
    private double amount;
    private String paymentRefNumber;
    private String parkingSpotName;


    public ParkingTicketResponse(ParkingTicket ticket){
        this.ticketNumber = ticket.getTicketNumber();
        this.entryTimestamp = ticket.getEntryTimestamp();
        this.exitTimeStamp = ticket.getExitTimeStamp();
        this.amount = ticket.getAmount();
        this.paymentRefNumber = (ticket.getPayment() == null ? null : ticket.getPayment().getRefNumber());
        this.parkingSpotName = (ticket.getAssignedSpot() == null ? null : ticket.getAssignedSpot().getName());
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public ZonedDateTime getEntryTimestamp() {
        return entryTimestamp;
    }

    public ZonedDateTime getExitTimeStamp() {
        return exitTimeStamp;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentRefNumber() {
        return paymentRefNumber;
    }

    public String getParkingSpotName() {
        return parkingSpotName;
    }
}
