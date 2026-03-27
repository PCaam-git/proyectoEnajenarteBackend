package com.svalero.enajenarte.exception;

public class ContactMessageNotFoundException extends Exception {

    public ContactMessageNotFoundException() {
        super("The contact message does not exist");
    }
}