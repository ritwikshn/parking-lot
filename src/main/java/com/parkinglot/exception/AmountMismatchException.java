package com.parkinglot.exception;

public class AmountMismatchException extends RuntimeException{
    public AmountMismatchException(String s){
        super(s);
    }
}
