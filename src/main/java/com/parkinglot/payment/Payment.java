package com.parkinglot.payment;

import com.parkinglot.exception.PermanentPaymentException;

import java.time.ZonedDateTime;

public abstract class Payment {
    private double amount;
    private String refNumber;
    private PaymentStatus status;
    private ZonedDateTime lastUpdated;

    public abstract boolean processPayment() throws PermanentPaymentException;

    public PaymentStatus getStatus(){
        return status;
    }

    public double getAmount() {
        return amount;
    }

    public Payment(){}

    public Payment(double amount){
        this.amount = amount;
    }

    void setStatus(PaymentStatus status) {
        this.lastUpdated = ZonedDateTime.now();
        this.status = status;
    }

    public String getRefNumber() {
        return refNumber;
    }
}
