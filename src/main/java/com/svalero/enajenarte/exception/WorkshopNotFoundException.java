package com.svalero.enajenarte.exception;

public class WorkshopNotFoundException extends Exception{
    public WorkshopNotFoundException() {
        super("El taller no existe");
    }
}
