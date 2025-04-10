package com.parkinglot;

import com.parkinglot.parkingspot.ParkingSpot;
import com.parkinglot.payment.Payment;

import java.time.ZonedDateTime;

public class ParkingTicket {
    private int ticketNumber;
    private ZonedDateTime entryTimestamp;
    private ZonedDateTime exitTimeStamp;
    private double amount;
    private Payment payment;
    private ParkingSpot assignedSpot;

    ParkingTicket(int ticketNumber, ZonedDateTime entryTimestamp){
        this(ticketNumber, entryTimestamp, null);
    }
    ParkingTicket(int ticketNumber, ZonedDateTime entryTimestamp, ParkingSpot assignedSpot){
        this.ticketNumber = ticketNumber;
        this.entryTimestamp = entryTimestamp;
        this.assignedSpot = assignedSpot;
    }

    public ZonedDateTime getEntryTimestamp() {
        return entryTimestamp;
    }

    void setEntryTimestamp(ZonedDateTime entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
    }

    public ZonedDateTime getExitTimeStamp() {
        return exitTimeStamp;
    }

    void setExitTimeStamp(ZonedDateTime exitTimeStamp) {
        this.exitTimeStamp = exitTimeStamp;
    }

    public double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

    void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public ParkingSpot getAssignedSpot() {
        return assignedSpot;
    }

    void setAssignedSpot(ParkingSpot assignedSpot) {
        this.assignedSpot = assignedSpot;
    }
}
