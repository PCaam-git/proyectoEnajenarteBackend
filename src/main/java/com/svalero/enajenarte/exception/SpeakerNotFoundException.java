package com.svalero.enajenarte.exception;

public class SpeakerNotFoundException extends Exception{
    public SpeakerNotFoundException() {
        super("El ponente no existe");
    }
}
