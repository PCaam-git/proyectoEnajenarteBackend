package com.svalero.enajenarte.exception;

public class WorkshopNotFoundException extends Exception{
    public WorkshopNotFoundException() {
        super("The workshop does not exist");
    }
}
