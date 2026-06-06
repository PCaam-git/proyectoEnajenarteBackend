package com.svalero.enajenarte.exception;

public class AdminCalendarNotFoundException extends Exception{
    public AdminCalendarNotFoundException() {
        super("La entrada en el calendario no existe");
    }

}
