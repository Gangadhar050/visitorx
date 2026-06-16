package com.visitor_x.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PurposeOfVisit {

    MEETING("Meeting"),
    INTERVIEW("Interview"),
    INTERNSHIP("Internship"),
    FULL_TIME_EMPLOYMENT("Full Time Employment");

    private final String label;

    PurposeOfVisit(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static PurposeOfVisit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalizedValue = value.trim();

        for (PurposeOfVisit purpose : PurposeOfVisit.values()) {
            if (
                    purpose.name().equalsIgnoreCase(normalizedValue)
                            || purpose.label.equalsIgnoreCase(normalizedValue)
            ) {
                return purpose;
            }
        }

        throw new IllegalArgumentException("Invalid purposeOfVisit: " + value);
    }
}
