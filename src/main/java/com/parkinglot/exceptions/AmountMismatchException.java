package com.parkinglot.exceptions;

public class AmountMismatchException extends RuntimeException{
    public AmountMismatchException(String s){
        super(s);
    }
}
