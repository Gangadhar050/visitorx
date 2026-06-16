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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PurposeOfVisit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmedValue = value.trim();

        // Try matching by enum name first (e.g., "INTERVIEW")
        try {
            return PurposeOfVisit.valueOf(trimmedValue.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // If not found by name, try matching by label
            for (PurposeOfVisit purpose : PurposeOfVisit.values()) {
                if (purpose.label.equalsIgnoreCase(trimmedValue)) {
                    return purpose;
                }
            }
        }

        throw new IllegalArgumentException("Invalid purposeOfVisit: " + value);
    }
}
