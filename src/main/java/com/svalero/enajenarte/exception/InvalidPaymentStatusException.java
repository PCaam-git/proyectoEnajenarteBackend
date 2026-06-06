package com.svalero.enajenarte.exception;

public class InvalidPaymentStatusException  extends Exception{
    public InvalidPaymentStatusException() {
        super("Estado de pago inválido");
    }
}
