package com.svalero.enajenarte.exception;

public class ProgramCapacityExceededException extends Exception {

    public ProgramCapacityExceededException() {
        super("El programa ha alcanzado el máximo de participantes");
    }

    public ProgramCapacityExceededException(String message) {
        super(message);
    }
}