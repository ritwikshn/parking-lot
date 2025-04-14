package com.parkinglot.payment;

import com.parkinglot.exception.PermanentPaymentException;

public class CashPayment extends Payment {
    private final static int MAX_RETRIES = 3;
    private final static long RETRY_DELAY = 1000;
    public CashPayment(){
        super();
    }
    public CashPayment(double amount){
        super(amount);
    }
    private boolean simulatePayment() throws InterruptedException{
        //simulate payment
        //this may fail sometimes just as an actual payment can
        Thread.sleep(5000);
        return Math.random() < 0.8;
    }
    @Override
    public boolean processPayment() throws PermanentPaymentException {
        System.out.printf("Initiating cash payment amounting to %f. Please wait....\n", this.getAmount());

        for(int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            try {
                boolean success = simulatePayment();
                if(success) {
                    System.out.println("Payment successful ;)");
                    this.setStatus(PaymentStatus.COMPLETED);
                    return true;
                }
            } catch (InterruptedException e) {
                //permanent payment exception may be thrown when
                //the cash is fake and other situations like so
                this.setStatus(PaymentStatus.FAILED);
                throw new PermanentPaymentException("Interrupted while processing payment");
            }

            System.out.println("Payment attempt failed, retrying...");

            //exponential backoff
            try{
                Thread.sleep(RETRY_DELAY * attempt);
            } catch(InterruptedException e){
                Thread.currentThread().interrupt();
                this.setStatus(PaymentStatus.FAILED);
                return false;
            }
        }
        this.setStatus(PaymentStatus.FAILED);
        return false;
    }
}
