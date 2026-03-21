package com.svalero.enajenarte.exception;

public class SpeakerNotFoundException extends Exception{
    public SpeakerNotFoundException() {
        super("The speaker does not exist");
    }
}
