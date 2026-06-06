package com.svalero.enajenarte.exception;

public class ProgramNotFoundException extends Exception{
    public ProgramNotFoundException() {
        super("El programa no existe");
    }
}
