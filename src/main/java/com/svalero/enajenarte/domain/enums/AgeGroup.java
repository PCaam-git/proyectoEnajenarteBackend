package com.svalero.enajenarte.domain.enums;

public enum AgeGroup {
    UNDER_18("Menos de 18"),
    BETWEEN_18_24("18-24"),
    BETWEEN_25_34("25-34"),
    BETWEEN_35_44("35-44"),
    BETWEEN_45_54("45-54"),
    BETWEEN_55_64("55-64"),
    OVER_65("65 o más"),
    PREFER_NOT_TO_SAY("Prefiero no contestar");

    private final String displayName;

    AgeGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

