package com.svalero.enajenarte.exception;

public class ProgramNotFoundException extends Exception{
    public ProgramNotFoundException() {
        super("The program does not exist");
    }
}
