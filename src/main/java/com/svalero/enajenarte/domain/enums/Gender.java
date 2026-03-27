package com.svalero.enajenarte.domain.enums;

public enum Gender {
    FEMALE("Femenino"),
    MALE("Masculino"),
    OTHER("Otro"),
    PREFER_NOT_TO_SAY("Prefiero no contestar");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
