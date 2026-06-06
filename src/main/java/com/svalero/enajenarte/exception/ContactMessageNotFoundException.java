package com.svalero.enajenarte.exception;

public class ContactMessageNotFoundException extends Exception {

    public ContactMessageNotFoundException() {
        super("El mensaje de contacto no existe");
    }
}