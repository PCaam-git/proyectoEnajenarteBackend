package com.svalero.enajenarte.exception;

public class HasAssociatedRegistrationsException extends Exception {
    public HasAssociatedRegistrationsException() {
        super("No se puede eliminar: hay inscripciones asociadas");
    }
}