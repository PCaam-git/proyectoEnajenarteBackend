package com.svalero.enajenarte.exception;

public class HasAssociatedRegistrationsException extends Exception {
    public HasAssociatedRegistrationsException() {
        super("Cannot delete: there are associated registrations");
    }
}