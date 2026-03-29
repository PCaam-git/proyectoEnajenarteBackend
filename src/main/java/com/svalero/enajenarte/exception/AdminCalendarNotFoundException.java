package com.svalero.enajenarte.exception;

public class AdminCalendarNotFoundException extends Exception{
    public AdminCalendarNotFoundException() {
        super("The calendar does not exist");
    }

}
