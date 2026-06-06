package com.svalero.enajenarte.exception;

public class WorkshopCapacityExceededException extends Exception {

    public WorkshopCapacityExceededException() {
        super("El workshop ha alcanzado el máximo de participantes");
    }

    public WorkshopCapacityExceededException(String message) {
        super(message);
    }
}