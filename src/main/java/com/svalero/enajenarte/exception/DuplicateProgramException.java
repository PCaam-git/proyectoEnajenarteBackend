package com.svalero.enajenarte.exception;

public class DuplicateProgramException extends Exception {

    public DuplicateProgramException() {
        super("Ya existe un programa con el mismo nombre, fecha de inicio y en conflicto con la modalidad y el ponente");
    }
}