package com.svalero.enajenarte.exception;

public class InvalidDateRangeException extends Exception{
    public InvalidDateRangeException() {
        super("ConfirmationDeadline must be before a StartDate");
    }
}
