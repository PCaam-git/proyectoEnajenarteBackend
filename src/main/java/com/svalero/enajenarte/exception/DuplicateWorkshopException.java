package com.svalero.enajenarte.exception;

public class DuplicateWorkshopException extends Exception {

    public DuplicateWorkshopException() {
        super("A workshop with the same name, start date and conflicting modality or speaker already exists");
    }
}