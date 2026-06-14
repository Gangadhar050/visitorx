package com.visitor_x.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PurposeOfVisit {

    INTERVIEW("Interview"),
    INTERNSHIP("Internship"),
    FULL_TIME_EMPLOYEE("Full Time Employment"),
    BUSINESS_MEETING("Business Meeting"),
    MEETING("Meeting");

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
        for (PurposeOfVisit purpose : PurposeOfVisit.values()) {
            if (purpose.name().equalsIgnoreCase(value) ||
                    purpose.label.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Invalid purpose of visit: " + value);
    }
}