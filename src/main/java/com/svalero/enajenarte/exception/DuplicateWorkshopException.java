package com.svalero.enajenarte.exception;

public class DuplicateWorkshopException extends Exception {

    public DuplicateWorkshopException() {
        super("Ya existe un taller con el mismo nombre, fecha de inicio y un conflicto con la modalidad o el ponente");
    }
}