package com.parkinglot.exceptions;

public class PermanentPaymentException extends Exception{
    public PermanentPaymentException(String s){
        super(s);
    }
    public PermanentPaymentException(String s, Throwable cause){
        super(s, cause);
    }
}
