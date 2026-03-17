package com.svalero.enajenarte.exception;

public class WorkshopCapacityExceededException extends RuntimeException{
    public WorkshopCapacityExceededException() {
        super("El workshop ha alcazado el máximo de participantes");
    }
}
