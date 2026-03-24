package com.svalero.enajenarte.exception;

public class AccessDeniedException extends Exception {
    public AccessDeniedException() {
        super("No tienes permisos de acceso");
    }
}