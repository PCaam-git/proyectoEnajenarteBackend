package com.svalero.enajenarte.exception;

public class InvalidPaymentStatusException  extends Exception{
    public InvalidPaymentStatusException() {
        super("Invalid paymentStatus value");
    }
}
