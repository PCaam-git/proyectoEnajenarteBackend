package com.svalero.enajenarte.exception;

public class DuplicateProgramException extends Exception {

    public DuplicateProgramException() {
        super("A program with the same name, init date and conflicting modality or speaker already exists");
    }
}