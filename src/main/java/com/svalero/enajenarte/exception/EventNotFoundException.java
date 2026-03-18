package com.svalero.enajenarte.exception;

public class EventNotFoundException extends Exception {
    public EventNotFoundException() {
        super("The event does not exist");
    }
}
